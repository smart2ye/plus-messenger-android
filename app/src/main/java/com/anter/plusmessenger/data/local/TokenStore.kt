package com.anter.plusmessenger.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "plus_messenger_prefs")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_TOKEN = stringPreferencesKey("auth_token")
    private val KEY_USER_ID = intPreferencesKey("user_id")
    private val KEY_USERNAME = stringPreferencesKey("username")
    private val KEY_NAME = stringPreferencesKey("name")
    private val KEY_AVATAR = stringPreferencesKey("avatar")

    suspend fun getToken(): String? = context.dataStore.data.map { it[KEY_TOKEN] }.first()

    suspend fun saveSession(
        token: String,
        userId: Int,
        username: String,
        name: String?,
        avatar: String?
    ) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_USER_ID] = userId
            it[KEY_USERNAME] = username
            it[KEY_NAME] = name ?: ""
            it[KEY_AVATAR] = avatar ?: ""
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
