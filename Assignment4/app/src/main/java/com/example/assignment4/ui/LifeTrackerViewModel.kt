package com.example.assignment4.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LifecycleEvent(
    val state: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class LifeTrackerUiState(
    val currentState: String = "onCreate",
    val events: List<LifecycleEvent> = emptyList()
)

class LifeTrackerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LifeTrackerUiState())
    val uiState: StateFlow<LifeTrackerUiState> = _uiState.asStateFlow()

    fun logLifecycleEvent(state: String) {
        val currentState = _uiState.value
        val newEvent = LifecycleEvent(state = state)
        _uiState.value = currentState.copy(
            currentState = state,
            events = currentState.events + newEvent
        )
    }
}
