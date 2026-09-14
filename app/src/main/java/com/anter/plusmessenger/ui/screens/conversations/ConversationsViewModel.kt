package com.anter.plusmessenger.ui.screens.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.api.models.ConversationDto
import com.anter.plusmessenger.data.repository.ConversationsRepository
import com.anter.plusmessenger.data.repository.ConversationsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ConversationsUiState(
    val loading: Boolean = true,
    val items: List<ConversationDto> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val repo: ConversationsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ConversationsUiState())
    val state: StateFlow<ConversationsUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            when (val r = repo.load()) {
                is ConversationsResult.Success -> _state.value = ConversationsUiState(
                    loading = false, items = r.items
                )
                is ConversationsResult.Error -> _state.value = ConversationsUiState(
                    loading = false, error = r.message
                )
            }
        }
    }
}
