package com.example.android.mytodo.data

import kotlinx.coroutines.flow.Flow

class OfflineToDoRepository(private val toDoDao: ToDoDao): ToDoRepository {
    override fun getAllTodosStream(): Flow<List<ToDo>> {
        return toDoDao.getAllToDos()
    }

    override fun getToDoStream(id: Int): Flow<ToDo?> {
        return toDoDao.getToDo(id)
    }

    override suspend fun insertToDo(todo: ToDo) {
        return toDoDao.insert(todo)
    }

    override suspend fun updateToDo(todo: ToDo) {
        return toDoDao.update(todo)
    }

    override suspend fun deleteToDo(todo: ToDo) {
        return toDoDao.delete(todo)
    }

    override suspend fun deleteAllToDos() {
        return toDoDao.deleteAllToDos()
    }
}