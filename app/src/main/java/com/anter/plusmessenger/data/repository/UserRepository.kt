package com.anter.plusmessenger.data.repository

import com.anter.plusmessenger.data.api.AnterApi
import com.anter.plusmessenger.data.api.models.ApiError
import com.anter.plusmessenger.data.api.models.ReportUserRequest
import com.anter.plusmessenger.data.api.models.UserProfileDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

sealed class ProfileResult {
    data class Success(val user: UserProfileDto) : ProfileResult()
    data class Error(val message: String) : ProfileResult()
}

sealed class ReportResult {
    data class Success(val reportId: Int?) : ReportResult()
    data class Error(val message: String) : ReportResult()
}

@Singleton
class UserRepository @Inject constructor(
    private val api: AnterApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiError::class.java)

    suspend fun loadProfile(username: String): ProfileResult {
        return try {
            val resp = api.getUserProfile(username)
            val u = resp.user
            if (u == null) {
                ProfileResult.Error(resp.error ?: "لم يتم العثور على المستخدم.")
            } else {
                ProfileResult.Success(u)
            }
        } catch (e: HttpException) {
            ProfileResult.Error(parseError(e) ?: "فشل تحميل البروفايل (${e.code()}).")
        } catch (e: Exception) {
            ProfileResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun reportUser(username: String, reason: String, details: String?): ReportResult {
        return try {
            val resp = api.reportUser(username, ReportUserRequest(reason, details))
            if (resp.success) {
                ReportResult.Success(resp.reportId)
            } else {
                ReportResult.Error(resp.error ?: "فشل إرسال البلاغ.")
            }
        } catch (e: HttpException) {
            ReportResult.Error(parseError(e) ?: "فشل إرسال البلاغ (${e.code()}).")
        } catch (e: Exception) {
            ReportResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    private fun parseError(e: HttpException): String? {
        return try {
            val body = e.response()?.errorBody()?.string() ?: return null
            errorAdapter.fromJson(body)?.error
        } catch (_: Throwable) {
            null
        }
    }
}
