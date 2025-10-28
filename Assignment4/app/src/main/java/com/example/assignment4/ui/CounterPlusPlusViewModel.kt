package com.example.assignment4.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CounterPlusPlusUiState(
    val count: Int = 0,
    val isAutoMode: Boolean = false,
    val autoInterval: Long = 3000, // 3 seconds in milliseconds
    val status: String = "Auto mode: OFF"
)

class CounterPlusPlusViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CounterPlusPlusUiState())
    val uiState: StateFlow<CounterPlusPlusUiState> = _uiState.asStateFlow()
    
    private var autoIncrementJob: Job? = null

    fun increment() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(count = currentState.count + 1)
    }

    fun decrement() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(count = currentState.count - 1)
    }

    fun reset() {
        _uiState.value = _uiState.value.copy(count = 0)
    }

    fun toggleAutoMode() {
        val currentState = _uiState.value
        val newAutoMode = !currentState.isAutoMode
        
        if (newAutoMode) {
            startAutoIncrement()
        } else {
            stopAutoIncrement()
        }
        
        _uiState.value = currentState.copy(
            isAutoMode = newAutoMode,
            status = if (newAutoMode) "Auto mode: ON" else "Auto mode: OFF"
        )
    }

    fun setAutoInterval(intervalMs: Long) {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(autoInterval = intervalMs)
        
        // Restart auto increment if it's currently running
        if (currentState.isAutoMode) {
            stopAutoIncrement()
            startAutoIncrement()
        }
    }

    private fun startAutoIncrement() {
        autoIncrementJob = viewModelScope.launch {
            while (true) {
                delay(_uiState.value.autoInterval)
                increment()
            }
        }
    }

    private fun stopAutoIncrement() {
        autoIncrementJob?.cancel()
        autoIncrementJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoIncrement()
    }
}
