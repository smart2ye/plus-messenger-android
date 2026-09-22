package com.anter.plusmessenger.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MessageDao {
    @Query("SELECT * FROM cached_messages WHERE conversationUsername = :u ORDER BY id ASC LIMIT 500")
    suspend fun getAll(u: String): List<CachedMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedMessageEntity>)

    @Query("DELETE FROM cached_messages WHERE conversationUsername = :u")
    suspend fun clearConversation(u: String)

    @Query("SELECT MAX(id) FROM cached_messages WHERE conversationUsername = :u")
    suspend fun maxId(u: String): Int?
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM cached_conversations ORDER BY sortedAtEpoch DESC")
    suspend fun getAll(): List<CachedConversationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedConversationEntity>)

    @Query("DELETE FROM cached_conversations")
    suspend fun clearAll()
}
