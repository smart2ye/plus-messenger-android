package com.anter.plusmessenger.data.api

import com.anter.plusmessenger.data.local.TokenStore
import com.anter.plusmessenger.util.AvatarUtil
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * يستبدل عنوان الخادم في كل طلب بالعنوان المحفوظ في TokenStore.
 * يسمح بالتبديل بين الإنتاج والمحلي دون إعادة بناء APK.
 */
@Singleton
class ServerUrlInterceptor @Inject constructor(
    private val tokenStore: TokenStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val serverUrl = runBlocking { tokenStore.getServerUrl() }
        val base = serverUrl.toHttpUrlOrNull() ?: return chain.proceed(chain.request())

        // نُبلّغ AvatarUtil ليستخدم نفس الخادم في روابط الصور.
        AvatarUtil.setBase(base.toString())

        val original = chain.request()
        val oldUrl = original.url

        val newUrl = base.newBuilder()
            .encodedPath(oldUrl.encodedPath)
            .apply {
                oldUrl.queryParameterNames.forEach { name ->
                    oldUrl.queryParameterValues(name).forEach { v ->
                        if (v != null) addQueryParameter(name, v)
                    }
                }
            }
            .build()

        return chain.proceed(
            original.newBuilder().url(newUrl).build()
        )
    }
}
