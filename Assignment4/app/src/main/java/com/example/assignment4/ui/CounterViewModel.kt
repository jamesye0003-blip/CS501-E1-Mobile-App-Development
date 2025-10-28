package com.example.assignment4

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CounterUiState(
    val count: Int = 0,
    val stepSize: Int = 1,
    val isGoalReached: Boolean = false,
    val isBelowZero: Boolean = false
)

class CounterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CounterUiState())
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    fun increment() {
        val currentState = _uiState.value
        if (!currentState.isGoalReached) {
            val newCount = currentState.count + currentState.stepSize
            _uiState.value = currentState.copy(
                count = newCount,
                isGoalReached = newCount >= 100,
                isBelowZero = newCount < 0
            )
        }
    }

    fun decrement() {
        val currentState = _uiState.value
        val newCount = currentState.count - currentState.stepSize
        _uiState.value = currentState.copy(
            count = newCount,
            isGoalReached = newCount >= 100,
            isBelowZero = newCount < 0
        )
    }

    fun reset() {
        _uiState.value = CounterUiState(stepSize = _uiState.value.stepSize)
    }

    fun setStepSize(stepSize: Int) {
        _uiState.value = _uiState.value.copy(stepSize = stepSize)
    }
}
