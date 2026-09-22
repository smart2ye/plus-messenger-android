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


@JsonClass(generateAdapter = true)
data class ContactsResponse(
    val contacts: List<UserDto>? = emptyList(),
    val error: String? = null
)


@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    val user: UserProfileDto? = null,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class UserProfileDto(
    val id: Int,
    val username: String,
    val name: String? = null,
    val avatar: String? = null,
    @Json(name = "isOnline") val isOnline: Boolean? = false,
    val bio: String? = null,
    @Json(name = "lastSeen") val lastSeen: String? = null,
    @Json(name = "isFollowing") val isFollowing: Boolean = false,
    @Json(name = "isFollowedBy") val isFollowedBy: Boolean = false,
    @Json(name = "isBlocked") val isBlocked: Boolean = false,
    @Json(name = "isMutual") val isMutual: Boolean = false,
    @Json(name = "isSelf") val isSelf: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ReportUserRequest(
    val reason: String,
    val details: String? = null
)

@JsonClass(generateAdapter = true)
data class ReportUserResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "reportId") val reportId: Int? = null,
    val error: String? = null
)


@JsonClass(generateAdapter = true)
data class SettingsResponse(
    @Json(name = "profileVisibility") val profileVisibility: String = "public",
    @Json(name = "wallVisibility") val wallVisibility: String = "public",
    @Json(name = "hideFollowers") val hideFollowers: Boolean = false,
    @Json(name = "showOnlineStatus") val showOnlineStatus: Boolean = true,
    @Json(name = "messagePrivacy") val messagePrivacy: String = "everyone",
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateSettingsRequest(
    @Json(name = "profileVisibility") val profileVisibility: String? = null,
    @Json(name = "wallVisibility") val wallVisibility: String? = null,
    @Json(name = "hideFollowers") val hideFollowers: Boolean? = null,
    @Json(name = "showOnlineStatus") val showOnlineStatus: Boolean? = null,
    @Json(name = "messagePrivacy") val messagePrivacy: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateSettingsResponse(
    @Json(name = "success") val success: Boolean = false,
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class BlockedUsersResponse(
    val blocked: List<BlockedUserDto>? = emptyList(),
    val error: String? = null
)

@JsonClass(generateAdapter = true)
data class BlockedUserDto(
    val id: Int,
    val username: String,
    val name: String? = null,
    val avatar: String? = null,
    @Json(name = "isOnline") val isOnline: Boolean? = false,
    val bio: String? = null,
    @Json(name = "lastSeen") val lastSeen: String? = null,
    @Json(name = "blockedAt") val blockedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class BlockResponse(
    @Json(name = "success") val success: Boolean = false,
    @Json(name = "isBlocked") val isBlocked: Boolean = false,
    val error: String? = null
)
