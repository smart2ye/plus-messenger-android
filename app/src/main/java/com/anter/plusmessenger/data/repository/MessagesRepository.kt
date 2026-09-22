package com.anter.plusmessenger.data.repository

import com.anter.plusmessenger.data.api.AnterApi
import com.anter.plusmessenger.data.api.models.ApiError
import com.anter.plusmessenger.data.api.models.MessageDto
import com.anter.plusmessenger.data.api.models.SendMessageRequest
import com.anter.plusmessenger.data.api.models.TypingRequest
import com.anter.plusmessenger.data.api.models.UserDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

sealed class MessagesResult {
    data class Success(val user: UserDto?, val messages: List<MessageDto>) : MessagesResult()
    data class Error(val message: String) : MessagesResult()
}

sealed class SendResult {
    data class Success(val message: MessageDto, val assistantMessage: MessageDto?) : SendResult()
    data class Error(val message: String) : SendResult()
}

@Singleton
class MessagesRepository @Inject constructor(
    private val api: AnterApi
) {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val errorAdapter = moshi.adapter(ApiError::class.java)

    suspend fun load(username: String, afterId: Int): MessagesResult {
        return try {
            val resp = api.getMessages(username, afterId)
            if (!resp.error.isNullOrBlank()) {
                return MessagesResult.Error(resp.error)
            }
            MessagesResult.Success(resp.user, resp.messages ?: emptyList())
        } catch (e: HttpException) {
            MessagesResult.Error(parseError(e) ?: "فشل تحميل الرسائل (${e.code()}).")
        } catch (e: Exception) {
            MessagesResult.Error(e.message ?: "تعذر تحميل الرسائل.")
        }
    }

    suspend fun send(username: String, content: String): SendResult {
        return try {
            val resp = api.sendMessage(username, SendMessageRequest(content = content))
            val msg = resp.message
            if (msg == null) {
                SendResult.Error(resp.error ?: "لم يتم استلام الرسالة من الخادم.")
            } else {
                SendResult.Success(msg, resp.assistantMessage)
            }
        } catch (e: HttpException) {
            SendResult.Error(parseError(e) ?: "فشل إرسال الرسالة (${e.code()}).")
        } catch (e: Exception) {
            SendResult.Error(e.message ?: "تعذر إرسال الرسالة.")
        }
    }

    suspend fun sendTyping(username: String, isTyping: Boolean): Boolean {
        return try {
            api.sendTyping(username, TypingRequest(isTyping)).isTyping
        } catch (_: Throwable) {
            false
        }
    }

    suspend fun isOtherTyping(username: String): Boolean {
        return try {
            api.getActivity(username).isTyping
        } catch (_: Throwable) {
            false
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
