package com.example.kmptodo.android.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TodoEmptyState() {
    Text(
        text = "No TODOs yet",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Preview(showBackground = true)
@Composable
private fun TodoEmptyStatePreview() {
    TodoPreviewTheme {
        TodoEmptyState()
    }
}
