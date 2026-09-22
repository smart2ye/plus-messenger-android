package com.anter.plusmessenger.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.local.TokenStore
import com.anter.plusmessenger.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val checking: Boolean = true,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val token = tokenStore.getToken()
            _state.value = AuthState(
                checking = false,
                isLoggedIn = !token.isNullOrBlank()
            )
        }
    }

    fun markLoggedIn() {
        _state.value = AuthState(checking = false, isLoggedIn = true)
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            repo.logout()
            _state.value = AuthState(checking = false, isLoggedIn = false)
            onDone()
        }
    }
}
