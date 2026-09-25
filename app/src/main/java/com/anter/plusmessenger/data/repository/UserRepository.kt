package com.anter.plusmessenger.data.repository

import com.anter.plusmessenger.data.api.AnterApi
import com.anter.plusmessenger.data.api.models.ApiError
import com.anter.plusmessenger.data.api.models.BlockedUserDto
import com.anter.plusmessenger.data.api.models.FindFriendDto
import com.anter.plusmessenger.data.api.models.FindFriendsRequest
import com.anter.plusmessenger.data.api.models.ReportUserRequest
import com.anter.plusmessenger.data.api.models.UpdateSettingsRequest
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

sealed class SettingsResult {
    data class Success(val settings: com.anter.plusmessenger.data.api.models.SettingsResponse) : SettingsResult()
    data class Error(val message: String) : SettingsResult()
}

sealed class BlockedUsersResult {
    data class Success(val items: List<BlockedUserDto>) : BlockedUsersResult()
    data class Error(val message: String) : BlockedUsersResult()
}

sealed class FindFriendsResult {
    data class Success(val suggested: List<FindFriendDto>, val matched: Int) : FindFriendsResult()
    data class Error(val message: String) : FindFriendsResult()
}

sealed class SimpleResult {
    data class Success(val isBlocked: Boolean? = null) : SimpleResult()
    data class Error(val message: String) : SimpleResult()
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

    suspend fun loadSettings(): SettingsResult {
        return try {
            val resp = api.getSettings()
            SettingsResult.Success(resp)
        } catch (e: HttpException) {
            SettingsResult.Error(parseError(e) ?: "فشل تحميل الإعدادات (${e.code()}).")
        } catch (e: Exception) {
            SettingsResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun updateSettings(body: UpdateSettingsRequest): SimpleResult {
        return try {
            val resp = api.updateSettings(body)
            if (resp.success) SimpleResult.Success()
            else SimpleResult.Error(resp.error ?: "فشل حفظ الإعدادات.")
        } catch (e: HttpException) {
            SimpleResult.Error(parseError(e) ?: "فشل حفظ الإعدادات (${e.code()}).")
        } catch (e: Exception) {
            SimpleResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun loadBlockedUsers(): BlockedUsersResult {
        return try {
            val resp = api.getBlockedUsers()
            if (!resp.error.isNullOrBlank()) BlockedUsersResult.Error(resp.error)
            else BlockedUsersResult.Success(resp.blocked ?: emptyList())
        } catch (e: HttpException) {
            BlockedUsersResult.Error(parseError(e) ?: "فشل تحميل المحظورين (${e.code()}).")
        } catch (e: Exception) {
            BlockedUsersResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun blockUser(username: String): SimpleResult {
        return try {
            val resp = api.blockUser(username)
            if (resp.success) SimpleResult.Success(resp.isBlocked)
            else SimpleResult.Error(resp.error ?: "فشل الحظر.")
        } catch (e: HttpException) {
            SimpleResult.Error(parseError(e) ?: "فشل الحظر (${e.code()}).")
        } catch (e: Exception) {
            SimpleResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun unblockUser(username: String): SimpleResult {
        return try {
            val resp = api.unblockUser(username)
            if (resp.success) SimpleResult.Success(resp.isBlocked)
            else SimpleResult.Error(resp.error ?: "فشل رفع الحظر.")
        } catch (e: HttpException) {
            SimpleResult.Error(parseError(e) ?: "فشل رفع الحظر (${e.code()}).")
        } catch (e: Exception) {
            SimpleResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
        }
    }

    suspend fun findFriends(hashes: List<String>): FindFriendsResult {
        return try {
            val resp = api.findFriends(FindFriendsRequest(hashes))
            if (resp.success) {
                FindFriendsResult.Success(resp.suggested ?: emptyList(), resp.matched)
            } else {
                FindFriendsResult.Error(resp.error ?: "فشل البحث.")
            }
        } catch (e: HttpException) {
            FindFriendsResult.Error(parseError(e) ?: "فشل البحث (${e.code()}).")
        } catch (e: Exception) {
            FindFriendsResult.Error(e.message ?: "تعذر الاتصال بالخادم.")
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
