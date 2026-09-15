package com.anter.plusmessenger.data.repository

import com.anter.plusmessenger.data.api.AnterApi
import com.anter.plusmessenger.data.api.models.ApiError
import com.anter.plusmessenger.data.api.models.ConversationDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

sealed class ConversationsResult {
    data class Success(val items: List<ConversationDto>) : ConversationsResult()
    data class Error(val message: String) : ConversationsResult()
}

@Singleton
class ConversationsRepository @Inject constructor(
    private val api: AnterApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiError::class.java)

    suspend fun load(): ConversationsResult {
        return try {
            val resp = api.getConversations()
            if (!resp.error.isNullOrBlank()) {
                return ConversationsResult.Error(resp.error)
            }
            ConversationsResult.Success(resp.conversations ?: emptyList())
        } catch (e: HttpException) {
            ConversationsResult.Error(parseError(e) ?: "فشل تحميل المحادثات (${e.code()}).")
        } catch (e: Exception) {
            ConversationsResult.Error(e.message ?: "تعذر تحميل المحادثات.")
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
