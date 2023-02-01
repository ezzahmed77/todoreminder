package com.example.android.mytodo.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: ToDo)

    @Update
    suspend fun update(todo: ToDo)

    @Delete
    suspend fun delete(todo: ToDo)

    @Query("SELECT * FROM todo WHERE id = :id")
    fun getToDo(id: Int): Flow<ToDo>

    @Query("SELECT * FROM todo")
    fun getAllToDos(): Flow<List<ToDo>>

    @Query("DELETE FROM todo")
    suspend fun deleteAllToDos()
}
