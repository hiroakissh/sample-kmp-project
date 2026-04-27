package com.example.kmptodo.shared

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("todo.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}

object AndroidTodoGraph {
    @Volatile
    private var repository: TodoRepository? = null

    fun repository(context: Context): TodoRepository =
        repository ?: synchronized(this) {
            repository ?: TodoRepository(
                database = createAppDatabase(getDatabaseBuilder(context)),
            ).also { repository = it }
        }

    fun viewModelFactory(context: Context): ViewModelProvider.Factory =
        todoViewModelFactory(repository(context))
}
