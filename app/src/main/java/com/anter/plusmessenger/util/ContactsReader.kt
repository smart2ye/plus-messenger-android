package com.anter.plusmessenger.util

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import java.security.MessageDigest

/**
 * يقرأ جهات اتصال المستخدم ويُعيد بصمات SHA-256 للأرقام.
 * لا يُرسل الأرقام نفسها إلى الخادم — فقط البصمات.
 */
object ContactsReader {

    private const val MAX_CONTACTS = 500

    /** أكواد دولة نجرّبها عند تحويل الأرقام المحلية. */
    private val COMMON_COUNTRY_CODES = listOf(
        "967", "966", "971", "968", "973", "974", "965",
        "962", "961", "20", "964", "963", "218", "216",
        "213", "212", "249"
    )

    /** يُنفّذ على Thread منفصل — يقرأ القاعدة ويعيد قائمة بصمات. */
    fun readHashes(context: Context): List<String> {
        val result = LinkedHashSet<String>()

        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            null, null, null
        )

        cursor?.use {
            val idx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            if (idx < 0) return emptyList()
            while (it.moveToNext() && result.size < MAX_CONTACTS) {
                val raw = it.getString(idx) ?: continue
                for (v in normalizeVariants(raw)) {
                    sha256Hex(v)?.let(result::add)
                }
            }
        }
        return result.toList()
    }

    private fun normalizeVariants(raw: String): List<String> {
        val digits = raw.replace(Regex("[^0-9+]"), "")
        if (digits.isEmpty()) return emptyList()
        val variants = LinkedHashSet<String>()

        if (digits.startsWith("+")) {
            val cleaned = stripLeadingZero(digits)
            if (isValid(cleaned)) variants.add(cleaned)
            return variants.toList()
        }
        if (digits.startsWith("00") && digits.length > 4) {
            val cleaned = stripLeadingZero("+" + digits.substring(2))
            if (isValid(cleaned)) variants.add(cleaned)
            return variants.toList()
        }
        if (digits.startsWith("0")) {
            val rest = digits.trimStart('0')
            for (cc in COMMON_COUNTRY_CODES) {
                val candidate = "+" + cc + rest
                if (isValid(candidate)) variants.add(candidate)
            }
            return variants.toList()
        }
        for (cc in COMMON_COUNTRY_CODES) {
            if (digits.startsWith(cc)) {
                val candidate = "+" + digits
                if (isValid(candidate)) variants.add(candidate)
            }
        }
        return variants.toList()
    }

    private fun stripLeadingZero(s: String): String {
        if (s.length > 4 && s[1].isDigit() && s[2].isDigit() && s[3].isDigit() && s.getOrNull(4) == '0') {
            return s.substring(0, 4) + s.substring(5)
        }
        return s
    }

    private fun isValid(s: String): Boolean {
        if (!s.startsWith("+")) return false
        val d = s.substring(1)
        return d.all { it.isDigit() } && d.length in 8..15
    }

    private fun sha256Hex(input: String): String? = try {
        MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    } catch (_: Exception) { null }
}
