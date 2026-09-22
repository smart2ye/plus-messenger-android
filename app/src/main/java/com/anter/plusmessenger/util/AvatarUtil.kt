package com.anter.plusmessenger.util

import com.anter.plusmessenger.data.api.models.UserDto

object AvatarUtil {
    private const val BASE = "https://anter-1.onrender.com"

    /** يعيد رابط صورة البروفايل (الخادم يعرف كيف يخدم الصورة أو يعيد الافتراضية). */
    fun url(user: UserDto?): String {
        val id = user?.id ?: return "$BASE/static/img/default_avatar_male.png"
        return "$BASE/media/avatar/$id"
    }
}
