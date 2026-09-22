package com.anter.plusmessenger.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cached_messages",
    indices = [Index("conversationUsername")]
)
data class CachedMessageEntity(
    @PrimaryKey val id: Int,
    val conversationUsername: String,
    val json: String,
    val createdAtEpoch: Long
)

@Entity(tableName = "cached_conversations")
data class CachedConversationEntity(
    @PrimaryKey val username: String,
    val json: String,
    val sortedAtEpoch: Long
)
