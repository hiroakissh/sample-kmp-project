package com.example.kmptodo.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class TodoUiState(
    val todos: List<TodoItem> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
)

class ObservationHandle internal constructor(
    private val job: Job,
) {
    fun cancel() {
        job.cancel()
    }
}

class TodoViewModel(
    private val repository: TodoRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.todos
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load TODOs.",
                    )
                }
                .collect { todos ->
                    _uiState.value = TodoUiState(
                        todos = todos,
                        isLoading = false,
                    )
                }
        }
    }

    fun observeUiState(onEach: (TodoUiState) -> Unit): ObservationHandle {
        val job = viewModelScope.launch {
            uiState.collect { state -> onEach(state) }
        }
        return ObservationHandle(job)
    }

    fun addTodo(title: String) {
        val normalizedTitle = title.trim()
        if (normalizedTitle.isEmpty()) return

        viewModelScope.launch {
            repository.addTodo(normalizedTitle)
        }
    }

    fun setTodoDone(id: Long, isDone: Boolean) {
        viewModelScope.launch {
            repository.setTodoDone(id = id, isDone = isDone)
        }
    }

    fun deleteTodo(id: Long) {
        viewModelScope.launch {
            repository.deleteTodo(id = id)
        }
    }

    fun deleteDoneTodos() {
        viewModelScope.launch {
            repository.deleteDoneTodos()
        }
    }
}

fun todoViewModelFactory(repository: TodoRepository): ViewModelProvider.Factory =
    viewModelFactory {
        initializer {
            TodoViewModel(repository = repository)
        }
    }
