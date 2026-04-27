package com.example.kmptodo.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kmptodo.shared.AndroidTodoGraph
import com.example.kmptodo.shared.TodoItem
import com.example.kmptodo.shared.TodoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TodoRoute()
                }
            }
        }
    }
}

@Composable
private fun TodoRoute() {
    val context = LocalContext.current
    val viewModel: TodoViewModel = viewModel(
        factory = AndroidTodoGraph.viewModelFactory(context),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    TodoScreen(
        todos = state.todos,
        isLoading = state.isLoading,
        errorMessage = state.errorMessage,
        onAdd = viewModel::addTodo,
        onCheckedChange = viewModel::setTodoDone,
        onDelete = viewModel::deleteTodo,
        onDeleteDone = viewModel::deleteDoneTodos,
    )
}

@Composable
private fun TodoScreen(
    todos: List<TodoItem>,
    isLoading: Boolean,
    errorMessage: String?,
    onAdd: (String) -> Unit,
    onCheckedChange: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit,
    onDeleteDone: () -> Unit,
) {
    var title by remember { mutableStateOf("") }

    Scaffold { padding ->
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("New TODO") },
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        onAdd(title)
                        title = ""
                    },
                ) {
                    Text("Add")
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = onDeleteDone) {
                    Text("Clear done")
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(todos, key = { it.id }) { todo ->
                        TodoRow(
                            todo = todo,
                            onCheckedChange = onCheckedChange,
                            onDelete = onDelete,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoRow(
    todo: TodoItem,
    onCheckedChange: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = { checked -> onCheckedChange(todo.id, checked) },
            )
            Text(
                text = todo.title,
                modifier = Modifier.weight(1f),
                textDecoration = if (todo.isDone) TextDecoration.LineThrough else null,
            )
            IconButton(onClick = { onDelete(todo.id) }) {
                Text("削除")
            }
        }
    }
}
