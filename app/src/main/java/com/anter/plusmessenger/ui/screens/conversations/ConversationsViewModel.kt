package com.anter.plusmessenger.ui.screens.conversations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.api.models.ConversationDto
import com.anter.plusmessenger.data.api.models.UserDto
import com.anter.plusmessenger.data.repository.ConversationsRepository
import com.anter.plusmessenger.data.repository.ContactsResult
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
    val contacts: List<UserDto> = emptyList(),
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
            val convResult = repo.load()
            val contactsResult = repo.loadContacts()

            when (convResult) {
                is ConversationsResult.Success -> {
                    val contacts = (contactsResult as? ContactsResult.Success)?.items ?: emptyList()
                    _state.value = ConversationsUiState(
                        loading = false,
                        items = convResult.items,
                        contacts = contacts
                    )
                }
                is ConversationsResult.Error -> _state.value = ConversationsUiState(
                    loading = false,
                    error = convResult.message
                )
            }
        }
    }
}
