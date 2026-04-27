package com.example.kmptodo.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmptodo.shared.TodoUiState

@Composable
fun TodoScreen(
    state: TodoUiState,
    inputTitle: String,
    onInputTitleChange: (String) -> Unit,
    onAddTodo: (String) -> Unit,
    onSetTodoDone: (Long, Boolean) -> Unit,
    onDeleteTodo: (Long) -> Unit,
    onDeleteDoneTodos: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "KMP TODO",
                style = MaterialTheme.typography.headlineMedium,
            )

            TodoInputRow(
                title = inputTitle,
                onTitleChange = onInputTitleChange,
                onAdd = { onAddTodo(inputTitle) },
            )

            if (state.errorMessage != null) {
                TodoErrorText(message = state.errorMessage!!)
            }

            if (state.isLoading) {
                TodoLoadingState()
            } else {
                Button(
                    onClick = onDeleteDoneTodos,
                    enabled = state.todos.any { it.isDone },
                ) {
                    Text("Clear done")
                }

                if (state.todos.isEmpty()) {
                    TodoEmptyState()
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.todos, key = { it.id }) { todo ->
                            TodoRow(
                                todo = todo,
                                onCheckedChange = onSetTodoDone,
                                onDelete = onDeleteTodo,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoScreenPreview() {
    TodoPreviewTheme {
        TodoScreen(
            state = previewTodoUiState,
            inputTitle = "Write preview",
            onInputTitleChange = {},
            onAddTodo = {},
            onSetTodoDone = { _, _ -> },
            onDeleteTodo = {},
            onDeleteDoneTodos = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyTodoScreenPreview() {
    TodoPreviewTheme {
        TodoScreen(
            state = previewEmptyTodoUiState,
            inputTitle = "",
            onInputTitleChange = {},
            onAddTodo = {},
            onSetTodoDone = { _, _ -> },
            onDeleteTodo = {},
            onDeleteDoneTodos = {},
        )
    }
}
