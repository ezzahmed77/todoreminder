package com.example.android.mytodo.data.repositories

import com.example.android.mytodo.data.model.Todo
import com.example.android.mytodo.data.TodoDao
import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl(private val toDoDao: TodoDao): TodoRepository {
    override fun getAllTodosStream(): Flow<List<Todo>> {
        return toDoDao.getAllToDos()
    }

    override fun getTodoStream(id: Int): Flow<Todo?> {
        return toDoDao.getToDo(id)
    }

    override suspend fun insertTodo(todo: Todo) {
        return toDoDao.insert(todo)
    }

    override suspend fun updateTodo(todo: Todo) {
        return toDoDao.update(todo)
    }

    override suspend fun deleteTodo(todo: Todo) {
        return toDoDao.delete(todo)
    }

    override suspend fun deleteAllTodos() {
        return toDoDao.deleteAllToDos()
    }
}