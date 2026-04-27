package com.example.kmptodo.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kmptodo.shared.AndroidTodoGraph
import com.example.kmptodo.shared.TodoViewModel

@Composable
fun TodoRoute(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: TodoViewModel = viewModel(
        factory = AndroidTodoGraph.viewModelFactory(context),
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var inputTitle by rememberSaveable { mutableStateOf("") }

    TodoScreen(
        state = state,
        inputTitle = inputTitle,
        onInputTitleChange = { inputTitle = it },
        onAddTodo = { title ->
            viewModel.addTodo(title)
            inputTitle = ""
        },
        onSetTodoDone = viewModel::setTodoDone,
        onDeleteTodo = viewModel::deleteTodo,
        onDeleteDoneTodos = viewModel::deleteDoneTodos,
        modifier = modifier,
    )
}
