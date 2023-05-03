package com.example.android.mytodo.ui.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.mytodo.data.repositories.TodoRepository
import kotlinx.coroutines.flow.*

class TodoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    todoRepository: TodoRepository
) : ViewModel(){

    private val todoId: Int = checkNotNull(savedStateHandle[ToDoDetailsDestination.todoIdArg])

    val uiState: StateFlow<TodoUiState> = todoRepository.getTodoStream(id = todoId)
        .filterNotNull()
        .map {
            it.toTodoUiState()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TodoUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}