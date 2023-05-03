package com.example.android.mytodo.data

import androidx.room.*
import com.example.android.mytodo.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("SELECT * FROM todo WHERE id = :id")
    fun getToDo(id: Int): Flow<Todo>

    @Query("SELECT * FROM todo")
    fun getAllToDos(): Flow<List<Todo>>

    @Query("DELETE FROM todo")
    suspend fun deleteAllToDos()
}
