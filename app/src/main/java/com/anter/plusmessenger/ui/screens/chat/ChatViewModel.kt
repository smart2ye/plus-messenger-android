package com.anter.plusmessenger.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anter.plusmessenger.data.api.models.MessageDto
import com.anter.plusmessenger.data.api.models.UserDto
import com.anter.plusmessenger.data.repository.MessagesRepository
import com.anter.plusmessenger.data.repository.MessagesResult
import com.anter.plusmessenger.data.repository.SendResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val loading: Boolean = true,
    val user: UserDto? = null,
    val messages: List<MessageDto> = emptyList(),
    val sending: Boolean = false,
    val otherTyping: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: MessagesRepository
) : ViewModel() {

    val username: String = savedStateHandle.get<String>("username") ?: ""

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    private var lastMessageId: Int = 0
    private var typingJob: Job? = null
    private var lastTypingSentAt: Long = 0L

    init {
        viewModelScope.launch {
            loadInitial()
        }
        startMessagePolling()
        startTypingPolling()
    }

    private fun startMessagePolling() {
        viewModelScope.launch {
            while (isActive) {
                delay(7000)
                loadNew()
            }
        }
    }

    private fun startTypingPolling() {
        viewModelScope.launch {
            while (isActive) {
                delay(5000)
                val typing = repo.isOtherTyping(username)
                if (typing != _state.value.otherTyping) {
                    _state.value = _state.value.copy(otherTyping = typing)
                }
            }
        }
    }

    private suspend fun loadInitial() {
        _state.value = _state.value.copy(loading = true, error = null)
        when (val r = repo.load(username, 0)) {
            is MessagesResult.Success -> {
                lastMessageId = r.messages.maxOfOrNull { it.id } ?: 0
                _state.value = _state.value.copy(
                    loading = false,
                    user = r.user,
                    messages = r.messages
                )
            }
            is MessagesResult.Error -> {
                _state.value = _state.value.copy(loading = false, error = r.message)
            }
        }
    }

    private suspend fun loadNew() {
        when (val r = repo.load(username, lastMessageId)) {
            is MessagesResult.Success -> {
                if (r.messages.isNotEmpty()) {
                    lastMessageId = r.messages.maxOfOrNull { it.id } ?: lastMessageId
                    val merged = (_state.value.messages + r.messages)
                        .distinctBy { it.id }
                        .sortedBy { it.id }
                    _state.value = _state.value.copy(
                        messages = merged,
                        user = r.user ?: _state.value.user
                    )
                }
            }
            is MessagesResult.Error -> { /* صامت */ }
        }
    }

    fun refresh() {
        viewModelScope.launch { loadInitial() }
    }

    fun send(content: String) {
        val text = content.trim()
        if (text.isEmpty() || _state.value.sending) return
        _state.value = _state.value.copy(sending = true, error = null)
        viewModelScope.launch {
            when (val r = repo.send(username, text)) {
                is SendResult.Success -> {
                    val merged = (_state.value.messages + listOfNotNull(r.message, r.assistantMessage))
                        .distinctBy { it.id }
                        .sortedBy { it.id }
                    lastMessageId = merged.maxOfOrNull { it.id } ?: lastMessageId
                    _state.value = _state.value.copy(messages = merged, sending = false)
                }
                is SendResult.Error -> {
                    _state.value = _state.value.copy(sending = false, error = r.message)
                }
            }
        }
    }

    fun onInputChanged(text: String) {
        if (text.isBlank()) return
        val now = System.currentTimeMillis()

        typingJob?.cancel()
        typingJob = viewModelScope.launch {
            // نُرسل "يكتب الآن" مرة واحدة كل 5 ثوانٍ كحد أقصى.
            if (now - lastTypingSentAt > 5000L) {
                repo.sendTyping(username, true)
                lastTypingSentAt = System.currentTimeMillis()
            }
            // إذا توقف المستخدم 5 ثوانٍ، نُبلّغ الخادم أنه توقف.
            delay(5000)
            repo.sendTyping(username, false)
            lastTypingSentAt = 0L
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
