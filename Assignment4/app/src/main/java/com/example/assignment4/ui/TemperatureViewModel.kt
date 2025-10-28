package com.example.assignment4.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class TemperatureReading(
    val timestamp: Long = System.currentTimeMillis(),
    val value: Float
)

data class TemperatureUiState(
    val readings: List<TemperatureReading> = emptyList(),
    val isGenerating: Boolean = false,
    val current: Float = 0f,
    val average: Float = 0f,
    val min: Float = 0f,
    val max: Float = 0f
)

class TemperatureViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TemperatureUiState())
    val uiState: StateFlow<TemperatureUiState> = _uiState.asStateFlow()
    
    private var temperatureJob: Job? = null
    private val minTemp = 65f
    private val maxTemp = 85f

    fun toggleDataGeneration() {
        val currentState = _uiState.value
        if (currentState.isGenerating) {
            stopDataGeneration()
        } else {
            startDataGeneration()
        }
    }

    private fun startDataGeneration() {
        temperatureJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)
            
            while (true) {
                delay(2000) // 2 seconds
                val newReading = TemperatureReading(value = Random.nextFloat() * (maxTemp - minTemp) + minTemp)
                addReading(newReading)
            }
        }
    }

    private fun stopDataGeneration() {
        temperatureJob?.cancel()
        temperatureJob = null
        _uiState.value = _uiState.value.copy(isGenerating = false)
    }

    private fun addReading(reading: TemperatureReading) {
        val currentState = _uiState.value
        val newReadings = (listOf(reading) + currentState.readings).take(20) // Keep only last 20 readings
        
        val values = newReadings.map { it.value }
        val current = values.firstOrNull() ?: 0f
        val average = values.average().toFloat()
        val min = values.minOrNull() ?: 0f
        val max = values.maxOrNull() ?: 0f
        
        _uiState.value = currentState.copy(
            readings = newReadings,
            current = current,
            average = average,
            min = min,
            max = max
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopDataGeneration()
    }
}
