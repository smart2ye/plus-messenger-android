package com.anter.plusmessenger.ui.screens.findfriends

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.api.models.FindFriendDto
import com.anter.plusmessenger.data.repository.FindFriendsResult
import com.anter.plusmessenger.data.repository.UserRepository
import com.anter.plusmessenger.util.ContactsReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class FindFriendsUiState(
    val loading: Boolean = false,
    val results: List<FindFriendDto> = emptyList(),
    val error: String? = null,
    val submitted: Int = 0,
    val matched: Int = 0,
    val hasSearched: Boolean = false
)

@HiltViewModel
class FindFriendsViewModel @Inject constructor(
    application: Application,
    private val repo: UserRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(FindFriendsUiState())
    val state: StateFlow<FindFriendsUiState> = _state.asStateFlow()

    fun search() {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)

        viewModelScope.launch {
            val hashes = withContext(Dispatchers.IO) {
                try {
                    ContactsReader.readHashes(getApplication())
                } catch (_: SecurityException) {
                    emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            }

            if (hashes.isEmpty()) {
                _state.value = _state.value.copy(
                    loading = false,
                    hasSearched = true,
                    error = "لم يتم العثور على جهات اتصال قابلة للبحث. تأكد من منح الإذن."
                )
                return@launch
            }

            when (val r = repo.findFriends(hashes)) {
                is FindFriendsResult.Success -> _state.value = _state.value.copy(
                    loading = false,
                    hasSearched = true,
                    results = r.suggested,
                    matched = r.matched,
                    submitted = hashes.size
                )
                is FindFriendsResult.Error -> _state.value = _state.value.copy(
                    loading = false,
                    hasSearched = true,
                    error = r.message
                )
            }
        }
    }
}
