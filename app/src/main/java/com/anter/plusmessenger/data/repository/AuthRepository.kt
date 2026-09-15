package com.anter.plusmessenger.data.repository

import com.anter.plusmessenger.data.api.AnterApi
import com.anter.plusmessenger.data.api.models.LoginRequest
import com.anter.plusmessenger.data.local.TokenStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val username: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

private data class ErrorBody(val error: String? = null)

@Singleton
class AuthRepository @Inject constructor(
    private val api: AnterApi,
    private val tokenStore: TokenStore
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ErrorBody::class.java)

    suspend fun login(identifier: String, password: String): AuthResult {
        return try {
            val resp = api.login(LoginRequest(identifier.trim(), password))
            val token = resp.accessToken
            if (token.isNullOrBlank() || resp.user == null) {
                return AuthResult.Error(resp.error ?: "بيانات الدخول غير صحيحة.")
            }
            tokenStore.saveSession(
                token = token,
                userId = resp.user.id,
                username = resp.user.username,
                name = resp.user.name,
                avatar = resp.user.avatar
            )
            AuthResult.Success(resp.user.username)
        } catch (e: HttpException) {
            val msg = parseError(e) ?: "فشل تسجيل الدخول (${e.code()})."
            AuthResult.Error(msg)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun logout() = tokenStore.clear()

    suspend fun isLoggedIn(): Boolean = !tokenStore.getToken().isNullOrBlank()

    private fun parseError(e: HttpException): String? {
        return try {
            val body = e.response()?.errorBody()?.string() ?: return null
            errorAdapter.fromJson(body)?.error
        } catch (_: Throwable) {
            null
        }
    }
}
