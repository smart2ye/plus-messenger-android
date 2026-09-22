package com.anter.plusmessenger.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.api.models.BlockedUserDto
import com.anter.plusmessenger.data.api.models.SettingsResponse
import com.anter.plusmessenger.data.api.models.UpdateSettingsRequest
import com.anter.plusmessenger.data.repository.BlockedUsersResult
import com.anter.plusmessenger.data.repository.SettingsResult
import com.anter.plusmessenger.data.repository.SimpleResult
import com.anter.plusmessenger.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val loading: Boolean = true,
    val settings: SettingsResponse? = null,
    val error: String? = null,
    val saving: Boolean = false,
    val savedMessage: String? = null,
    val blockedUsers: List<BlockedUserDto> = emptyList(),
    val blockedLoading: Boolean = false,
    val blockedError: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = repo.loadSettings()) {
                is SettingsResult.Success -> _state.value = _state.value.copy(
                    loading = false, settings = r.settings
                )
                is SettingsResult.Error -> _state.value = _state.value.copy(
                    loading = false, error = r.message
                )
            }
        }
    }

    fun save(body: UpdateSettingsRequest) {
        if (_state.value.saving) return
        _state.value = _state.value.copy(saving = true, savedMessage = null, error = null)
        viewModelScope.launch {
            when (val r = repo.updateSettings(body)) {
                is SimpleResult.Success -> {
                    _state.value = _state.value.copy(saving = false, savedMessage = "تم الحفظ")
                    load()
                }
                is SimpleResult.Error -> _state.value = _state.value.copy(
                    saving = false, error = r.message
                )
            }
        }
    }

    fun loadBlockedUsers() {
        _state.value = _state.value.copy(blockedLoading = true, blockedError = null)
        viewModelScope.launch {
            when (val r = repo.loadBlockedUsers()) {
                is BlockedUsersResult.Success -> _state.value = _state.value.copy(
                    blockedLoading = false, blockedUsers = r.items
                )
                is BlockedUsersResult.Error -> _state.value = _state.value.copy(
                    blockedLoading = false, blockedError = r.message
                )
            }
        }
    }

    fun unblock(username: String) {
        viewModelScope.launch {
            when (val r = repo.unblockUser(username)) {
                is SimpleResult.Success -> loadBlockedUsers()
                is SimpleResult.Error -> _state.value = _state.value.copy(
                    blockedError = r.message
                )
            }
        }
    }

    fun clearSavedMessage() {
        _state.value = _state.value.copy(savedMessage = null)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
