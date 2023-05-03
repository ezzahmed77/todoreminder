package com.example.android.mytodo.data.repositories

import com.example.android.mytodo.data.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {

    /**
     * get all todos from list
     */
    fun getAllTodosStream(): Flow<List<Todo>>

    /**
     * Get one todoItem by its Id
     */
    fun getTodoStream(id: Int): Flow<Todo?>

    /**
     * Insert a new todoItem in the database
     */
    suspend fun insertTodo(todo: Todo)

    /**
     * Update an todoItem in database
     */
    suspend fun updateTodo(todo: Todo)

    /**
     * Delete a todoItem from database
     */
    suspend fun deleteTodo(todo: Todo)

    /**
     * Delete all todos from database
     */
    suspend fun deleteAllTodos()

}