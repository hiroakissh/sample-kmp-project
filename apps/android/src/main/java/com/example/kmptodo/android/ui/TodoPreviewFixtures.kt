package com.example.kmptodo.android.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.kmptodo.shared.TodoItem
import com.example.kmptodo.shared.TodoUiState

val previewActiveTodo = TodoItem(
    id = 1,
    title = "Buy milk",
    isDone = false,
)

val previewDoneTodo = TodoItem(
    id = 2,
    title = "Read KMP docs",
    isDone = true,
)

val previewTodoUiState = TodoUiState(
    todos = listOf(
        previewActiveTodo,
        previewDoneTodo,
        TodoItem(
            id = 3,
            title = "Open Compose preview",
            isDone = false,
        ),
    ),
    isLoading = false,
    errorMessage = null,
)

val previewEmptyTodoUiState = TodoUiState(
    todos = emptyList(),
    isLoading = false,
    errorMessage = null,
)

@Composable
fun TodoPreviewTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}
