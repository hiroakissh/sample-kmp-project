package com.example.kmptodo.shared

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepository(
    private val database: AppDatabase,
) {
    private val dao = database.todoDao()

    val todos: Flow<List<TodoItem>> =
        dao.observeTodos().map { entities ->
            entities.map { entity ->
                TodoItem(
                    id = entity.id,
                    title = entity.title,
                    isDone = entity.isDone,
                )
            }
        }

    suspend fun addTodo(title: String) {
        dao.insert(
            TodoEntity(
                title = title,
                createdAtEpochMillis = currentTimeMillis(),
            ),
        )
    }

    suspend fun setTodoDone(id: Long, isDone: Boolean) {
        dao.setDone(id = id, isDone = isDone)
    }

    suspend fun deleteTodo(id: Long) {
        dao.delete(id = id)
    }

    suspend fun deleteDoneTodos() {
        dao.deleteDone()
    }
}
