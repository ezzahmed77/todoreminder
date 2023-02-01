package com.example.android.mytodo.data

import kotlinx.coroutines.flow.Flow

interface ToDoRepository {

    /**
     * get all todos from list
     */
    fun getAllTodosStream(): Flow<List<ToDo>>

    /**
     * Get one todoItem by its Id
     */
    fun getToDoStream(id: Int): Flow<ToDo?>

    /**
     * Insert a new todoItem in the database
     */
    suspend fun insertToDo(todo: ToDo)

    /**
     * Update an todoItem in database
     */
    suspend fun updateToDo(todo: ToDo)

    /**
     * Delete a todoItem from database
     */
    suspend fun deleteToDo(todo: ToDo)

    /**
     * Delete all todos from database
     */
    suspend fun deleteAllToDos()

}