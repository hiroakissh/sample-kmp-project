package com.example.kmptodo.android.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TodoErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
    )
}

@Preview(showBackground = true)
@Composable
private fun TodoErrorTextPreview() {
    TodoPreviewTheme {
        TodoErrorText(message = "Failed to load TODOs.")
    }
}
