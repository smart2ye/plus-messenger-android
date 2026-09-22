package com.anter.plusmessenger.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CachedMessageEntity::class, CachedConversationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AnterDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
}
