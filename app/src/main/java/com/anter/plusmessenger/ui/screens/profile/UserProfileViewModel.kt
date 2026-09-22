package com.anter.plusmessenger.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.api.models.UserProfileDto
import com.anter.plusmessenger.data.repository.ProfileResult
import com.anter.plusmessenger.data.repository.ReportResult
import com.anter.plusmessenger.data.repository.SimpleResult
import com.anter.plusmessenger.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserProfileUiState(
    val loading: Boolean = true,
    val user: UserProfileDto? = null,
    val error: String? = null,
    val reportInFlight: Boolean = false,
    val reportSuccess: Boolean = false,
    val reportError: String? = null,
    val blockInFlight: Boolean = false,
    val blockMessage: String? = null
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: UserRepository
) : ViewModel() {

    val username: String = savedStateHandle.get<String>("username") ?: ""

    private val _state = MutableStateFlow(UserProfileUiState())
    val state: StateFlow<UserProfileUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = repo.loadProfile(username)) {
                is ProfileResult.Success -> _state.value = _state.value.copy(
                    loading = false, user = r.user
                )
                is ProfileResult.Error -> _state.value = _state.value.copy(
                    loading = false, error = r.message
                )
            }
        }
    }

    fun report(reason: String, details: String?) {
        if (_state.value.reportInFlight) return
        _state.value = _state.value.copy(
            reportInFlight = true,
            reportError = null,
            reportSuccess = false
        )
        viewModelScope.launch {
            when (val r = repo.reportUser(username, reason, details)) {
                is ReportResult.Success -> _state.value = _state.value.copy(
                    reportInFlight = false, reportSuccess = true
                )
                is ReportResult.Error -> _state.value = _state.value.copy(
                    reportInFlight = false, reportError = r.message
                )
            }
        }
    }

    fun toggleBlock() {
        val u = _state.value.user ?: return
        if (_state.value.blockInFlight) return
        _state.value = _state.value.copy(blockInFlight = true, blockMessage = null)
        viewModelScope.launch {
            val r = if (u.isBlocked) repo.unblockUser(u.username)
                    else repo.blockUser(u.username)
            when (r) {
                is SimpleResult.Success -> {
                    val nowBlocked = r.isBlocked ?: !u.isBlocked
                    _state.value = _state.value.copy(
                        blockInFlight = false,
                        blockMessage = if (nowBlocked) "تم حظر المستخدم." else "تم رفع الحظر.",
                        user = u.copy(isBlocked = nowBlocked, isMutual = if (nowBlocked) false else u.isMutual)
                    )
                }
                is SimpleResult.Error -> _state.value = _state.value.copy(
                    blockInFlight = false,
                    blockMessage = r.message
                )
            }
        }
    }

    fun clearBlockMessage() {
        _state.value = _state.value.copy(blockMessage = null)
    }

    fun clearReportResult() {
        _state.value = _state.value.copy(reportSuccess = false, reportError = null)
    }
}
