package com.example.kmptodo.android.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TodoLoadingState() {
    CircularProgressIndicator()
}

@Preview(showBackground = true)
@Composable
private fun TodoLoadingStatePreview() {
    TodoPreviewTheme {
        TodoLoadingState()
    }
}
