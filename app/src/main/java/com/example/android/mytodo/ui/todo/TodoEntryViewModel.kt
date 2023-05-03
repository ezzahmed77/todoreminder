package com.example.android.mytodo.ui.todo

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cancelNotification
import com.example.android.mytodo.data.model.Todo
import com.example.android.mytodo.data.repositories.TodoRepository
import kotlinx.coroutines.launch
import scheduleNotification

class TodoEntryViewModel(
    private val  context: Context,
    private val todoRepository: TodoRepository,
): ViewModel() {

    /**
     * Holds current todoItem ui state
     */
    var todoUiState by mutableStateOf(TodoUiState())
        private set

    /**
     * Updates the [todoUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(newTodoUiState: TodoUiState) {
        todoUiState = newTodoUiState.copy( actionEnabled = newTodoUiState.isValid())
    }
    /**
     * Saving item to database
     */
    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun saveToDo() {
        if(todoUiState.isValid()){
            viewModelScope.launch {
                todoRepository.insertTodo(todoUiState.toTodo())
                updateReminder(todoUiState.toTodo())
            }
        }
    }
    /**
     * Set reminder with alarm manager
     */


    @RequiresApi(Build.VERSION_CODES.S)
    private fun updateReminder(todo: Todo) {
        if(todo.hasReminder){
            scheduleNotification(context = context, notificationId = todo.id, todo = todo)
        }else{
            cancelNotification(context = context, todoId = todo.id)
        }
    }

}