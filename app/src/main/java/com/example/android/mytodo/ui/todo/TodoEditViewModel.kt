package com.example.android.mytodo.ui.todo

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cancelNotification
import com.example.android.mytodo.data.model.Todo
import com.example.android.mytodo.data.repositories.TodoRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import scheduleNotification

class TodoEditViewModel(
    val context: Context,
    savedStateHandle: SavedStateHandle,
    private val todoRepository: TodoRepository
) : ViewModel() {

    /**
     * Holds current todoItem ui state
     */
    var todoUiState by mutableStateOf(TodoUiState())
        private set

    private val todoId: Int = checkNotNull(savedStateHandle[ToDoEditDestination.toDoIdArg])

    /**
     * Getting the UiState
     */
    init {
        viewModelScope.launch {
            todoUiState = todoRepository.getTodoStream(id = todoId)
                .filterNotNull()
                .first()
                .toTodoUiState(actionEnabled = true)
        }
    }

    fun updateUiState(newTodoUiState: TodoUiState) {
        todoUiState = newTodoUiState.copy( actionEnabled = newTodoUiState.isValid())
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun updateToDo() {
        if (todoUiState.isValid()) {
            todoRepository.updateTodo(todoUiState.toTodo())
            updateReminder(todoUiState.toTodo())
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun updateReminder(todo: Todo) {
        if(todo.hasReminder){
            scheduleNotification(context = context, notificationId = todo.id, todo = todo)
        }else{
            cancelNotification(context = context, todoId = todo.id)
        }
    }


}
