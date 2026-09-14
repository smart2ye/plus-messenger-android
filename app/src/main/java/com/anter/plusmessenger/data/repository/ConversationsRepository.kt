package com.anter.plusmessenger.data.repository

import com.anter.plusmessenger.data.api.AnterApi
import com.anter.plusmessenger.data.api.models.ConversationDto
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
    suspend fun load(): ConversationsResult {
        return try {
            val resp = api.getConversations("")  // التوكن يُضاف من AuthInterceptor
            if (!resp.error.isNullOrBlank()) {
                return ConversationsResult.Error(resp.error)
            }
            ConversationsResult.Success(resp.conversations ?: emptyList())
        } catch (e: Exception) {
            ConversationsResult.Error(e.message ?: "تعذر تحميل المحادثات.")
        }
    }
}
