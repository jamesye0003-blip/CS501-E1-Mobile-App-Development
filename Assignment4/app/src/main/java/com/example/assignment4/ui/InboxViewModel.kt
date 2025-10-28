package com.example.assignment4

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Message(
    val id: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class InboxUiState(
    val messages: List<Message> = emptyList(),
    val isComposerVisible: Boolean = false,
    val composerText: String = "",
    val feedbackMessage: String? = null
)

class InboxViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InboxUiState())
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    fun toggleComposer() {
        _uiState.value = _uiState.value.copy(
            isComposerVisible = !_uiState.value.isComposerVisible,
            feedbackMessage = null
        )
    }

    fun updateComposerText(text: String) {
        _uiState.value = _uiState.value.copy(
            composerText = text,
            feedbackMessage = null
        )
    }

    fun addMessage() {
        val currentState = _uiState.value
        if (currentState.composerText.isNotBlank()) {
            val newMessage = Message(
                id = System.currentTimeMillis().toString(),
                text = currentState.composerText.trim()
            )
            _uiState.value = currentState.copy(
                messages = listOf(newMessage) + currentState.messages,
                composerText = "",
                isComposerVisible = false,
                feedbackMessage = "Message added"
            )
        }
    }

    fun deleteMessage(messageId: String) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            messages = currentState.messages.filter { it.id != messageId },
            feedbackMessage = "Message deleted"
        )
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}
