package com.example.kmptodo.android.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TodoInputRow(
    title: String,
    onTitleChange: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            label = { Text("New TODO") },
        )
        Spacer(Modifier.width(8.dp))
        Button(
            onClick = onAdd,
            enabled = title.isNotBlank(),
        ) {
            Text("Add")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoInputRowPreview() {
    TodoPreviewTheme {
        TodoInputRow(
            title = "Buy milk",
            onTitleChange = {},
            onAdd = {},
        )
    }
}
