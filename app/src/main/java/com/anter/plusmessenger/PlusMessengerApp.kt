package com.anter.plusmessenger

import android.app.Application
import android.os.Build
import android.os.Environment
import dagger.hilt.android.HiltAndroidApp
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltAndroidApp
class PlusMessengerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        installCrashLogger()
    }

    private fun installCrashLogger() {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val sw = StringWriter()
                throwable.printStackTrace(PrintWriter(sw))
                val stamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
                val header = buildString {
                    appendLine("===== CRASH REPORT =====")
                    appendLine("Time: $stamp")
                    appendLine("Thread: ${thread.name}")
                    appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                    appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                    appendLine("-----------------------")
                }
                val body = header + sw.toString()
                // نكتب في /storage/emulated/0/Android/data/<pkg>/files/crash-*.txt
                val dir = getExternalFilesDir(null)
                    ?: File(Environment.getExternalStorageDirectory(), "Android/data/$packageName/files")
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, "crash-$stamp.txt")
                file.writeText(body)
            } catch (_: Throwable) {
                // لا نفعل شيئًا إن فشل التسجيل
            }
            previous?.uncaughtException(thread, throwable)
        }
    }
}
