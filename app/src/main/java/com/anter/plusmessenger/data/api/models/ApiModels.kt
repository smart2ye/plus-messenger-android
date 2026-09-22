package com.anter.plusmessenger.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val identifier: String,
    val password: String,
    val deviceName: String = "Plus Messenger Android"
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "accessToken") val accessToken: String? = null,
    @Json(name = "tokenType") val tokenType: String? = null,
    @Json(name = "expiresAt") val expiresAt: String? = null,
    val user: UserDto? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: Int,
    val username: String,
    val name: String? = null,
    val avatar: String? = null,
    @Json(name = "isOnline") val isOnline: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class ConversationsResponse(
    val conversations: List<ConversationDto>? = emptyList(),
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ConversationDto(
    val user: UserDto,
    @Json(name = "latestMessage") val latestMessage: MessageDto? = null,
    @Json(name = "unreadCount") val unreadCount: Int = 0
)

@JsonClass(generateAdapter = true)
data class MessageDto(
    val id: Int,
    val content: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "senderId") val senderId: Int? = null,
    @Json(name = "receiverId") val receiverId: Int? = null,
    @Json(name = "isRead") val isRead: Boolean = false,
    @Json(name = "parentId") val parentId: Int? = null,
    val parent: ParentMessageDto? = null,
    val attachment: AttachmentDto? = null
)

@JsonClass(generateAdapter = true)
data class ParentMessageDto(
    val id: Int,
    @Json(name = "senderId") val senderId: Int? = null,
    val content: String? = null,
    @Json(name = "isDeletedEveryone") val isDeletedEveryone: Boolean = false
)

@JsonClass(generateAdapter = true)
data class AttachmentDto(
    val name: String? = null,
    @Json(name = "mimeType") val mimeType: String? = null,
    val kind: String? = null,
    @Json(name = "downloadPath") val downloadPath: String? = null
)

@JsonClass(generateAdapter = true)
data class MessagesResponse(
    val user: UserDto? = null,
    val messages: List<MessageDto>? = emptyList(),
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class SendMessageRequest(
    val content: String
)

@JsonClass(generateAdapter = true)
data class SendMessageResponse(
    val message: MessageDto? = null,
    @Json(name = "assistantMessage") val assistantMessage: MessageDto? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class TypingRequest(
    @Json(name = "isTyping") val isTyping: Boolean
)

@JsonClass(generateAdapter = true)
data class TypingResponse(
    @Json(name = "isTyping") val isTyping: Boolean = false,
    val error: String? = null
)
