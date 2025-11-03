package com.example.assignment5.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Note(
    val id: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Task(
    val id: String,
    val text: String,
    val isCompleted: Boolean = false
)

data class NotesUiState(
    val notes: List<Note> = emptyList()
)

data class TasksUiState(
    val tasks: List<Task> = emptyList()
)

class NotesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    fun addNote(text: String) {
        if (text.isNotBlank()) {
            val note = Note(
                id = System.currentTimeMillis().toString(),
                text = text
            )
            _uiState.value = _uiState.value.copy(
                notes = _uiState.value.notes + note
            )
        }
    }

    fun deleteNote(noteId: String) {
        _uiState.value = _uiState.value.copy(
            notes = _uiState.value.notes.filter { it.id != noteId }
        )
    }
}

class TasksViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun addTask(text: String) {
        if (text.isNotBlank()) {
            val task = Task(
                id = System.currentTimeMillis().toString(),
                text = text
            )
            _uiState.value = _uiState.value.copy(
                tasks = _uiState.value.tasks + task
            )
        }
    }

    fun toggleTask(taskId: String) {
        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.map { task ->
                if (task.id == taskId) {
                    task.copy(isCompleted = !task.isCompleted)
                } else {
                    task
                }
            }
        )
    }

    fun deleteTask(taskId: String) {
        _uiState.value = _uiState.value.copy(
            tasks = _uiState.value.tasks.filter { it.id != taskId }
        )
    }
}

