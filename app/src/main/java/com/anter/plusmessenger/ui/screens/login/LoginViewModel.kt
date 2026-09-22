package com.anter.plusmessenger.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.repository.AuthRepository
import com.anter.plusmessenger.data.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val identifier: String = "",
    val password: String = "",
    val serverUrl: String = "https://anter-1.onrender.com/",
    val showAdvanced: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    fun onIdentifierChange(v: String) { _state.value = _state.value.copy(identifier = v, error = null) }
    fun onPasswordChange(v: String) { _state.value = _state.value.copy(password = v, error = null) }
    fun onServerUrlChange(v: String) { _state.value = _state.value.copy(serverUrl = v, error = null) }
    fun toggleAdvanced() { _state.value = _state.value.copy(showAdvanced = !_state.value.showAdvanced) }

    fun submit() {
        val s = _state.value
        if (s.identifier.isBlank() || s.password.isBlank()) {
            _state.value = s.copy(error = "يرجى إدخال بيانات الدخول كاملة.")
            return
        }
        _state.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = repo.login(s.identifier, s.password, s.serverUrl)) {
                is AuthResult.Success -> _state.value = _state.value.copy(loading = false, success = true)
                is AuthResult.Error -> _state.value = _state.value.copy(loading = false, error = r.message)
            }
        }
    }
}
