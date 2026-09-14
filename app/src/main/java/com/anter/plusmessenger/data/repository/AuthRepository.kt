package com.anter.plusmessenger.data.repository

import com.anter.plusmessenger.data.api.AnterApi
import com.anter.plusmessenger.data.api.models.LoginRequest
import com.anter.plusmessenger.data.local.TokenStore
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult {
    data class Success(val username: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

@Singleton
class AuthRepository @Inject constructor(
    private val api: AnterApi,
    private val tokenStore: TokenStore
) {
    suspend fun login(identifier: String, password: String): AuthResult {
        return try {
            val resp = api.login(LoginRequest(identifier.trim(), password))
            if (!resp.error.isNullOrBlank() || resp.token.isNullOrBlank() || resp.user == null) {
                return AuthResult.Error(resp.error ?: "بيانات الدخول غير صحيحة.")
            }
            tokenStore.saveSession(
                token = resp.token,
                userId = resp.user.id,
                username = resp.user.username,
                name = resp.user.name,
                avatar = resp.user.avatar
            )
            AuthResult.Success(resp.user.username)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun logout() = tokenStore.clear()

    suspend fun isLoggedIn(): Boolean = !tokenStore.getToken().isNullOrBlank()
}
