package com.anter.plusmessenger.util

import com.anter.plusmessenger.data.api.models.UserDto

object AvatarUtil {
    private const val DEFAULT_BASE = "https://anter-1.onrender.com"

    /** يحدّثه ServerUrlInterceptor عند كل طلب — ليتبع الخادم النشط. */
    @Volatile
    private var base: String = DEFAULT_BASE

    fun setBase(url: String) {
        base = url.trimEnd('/')
    }

    /** يعيد رابط صورة البروفايل (الخادم يعرف كيف يخدم الصورة أو يعيد الافتراضية). */
    fun url(user: UserDto?): String {
        val id = user?.id ?: return "$base/static/img/default_avatar_male.png"
        return "$base/media/avatar/$id"
    }

    /** نسخة مباشرة تأخذ id بدل UserDto — تُستخدم في شاشات نتائج البحث. */
    fun urlById(id: Int?): String {
        if (id == null) return "$base/static/img/default_avatar_male.png"
        return "$base/media/avatar/$id"
    }
}
