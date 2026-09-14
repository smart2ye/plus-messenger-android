package com.anter.plusmessenger.data.api.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val identifier: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    @Json(name = "token") val token: String?,
    @Json(name = "user") val user: UserDto?,
    @Json(name = "error") val error: String?
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: Int,
    val username: String,
    val name: String?,
    val avatar: String?,
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
    @Json(name = "latestMessage") val latestMessage: MessageDto?,
    @Json(name = "unreadCount") val unreadCount: Int = 0
)

@JsonClass(generateAdapter = true)
data class MessageDto(
    val id: Int,
    val content: String?,
    @Json(name = "createdAt") val createdAt: String?,
    @Json(name = "senderId") val senderId: Int?,
    @Json(name = "receiverId") val receiverId: Int?
)
