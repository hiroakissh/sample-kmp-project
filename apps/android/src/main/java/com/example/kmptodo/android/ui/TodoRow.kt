package com.example.kmptodo.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kmptodo.shared.TodoItem

@Composable
fun TodoRow(
    todo: TodoItem,
    onCheckedChange: (Long, Boolean) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
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

@Preview(showBackground = true)
@Composable
private fun ActiveTodoRowPreview() {
    TodoPreviewTheme {
        TodoRow(
            todo = previewActiveTodo,
            onCheckedChange = { _, _ -> },
            onDelete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DoneTodoRowPreview() {
    TodoPreviewTheme {
        TodoRow(
            todo = previewDoneTodo,
            onCheckedChange = { _, _ -> },
            onDelete = {},
        )
    }
}
