package com.example.kmptodo.shared

import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val dbFilePath = "${documentDirectory()}/todo.db"
    return Room.databaseBuilder<AppDatabase>(
        name = dbFilePath,
    )
}

object IosTodoGraph {
    val repository: TodoRepository by lazy {
        TodoRepository(
            database = createAppDatabase(getDatabaseBuilder()),
        )
    }

    fun viewModelFactory(): ViewModelProvider.Factory =
        todoViewModelFactory(repository)
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
