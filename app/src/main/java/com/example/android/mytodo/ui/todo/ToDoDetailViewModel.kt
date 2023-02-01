package com.example.android.mytodo.ui.todo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.mytodo.data.ToDoRepository
import kotlinx.coroutines.flow.*

class ToDoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    toDoRepository: ToDoRepository
) : ViewModel(){

    private val todoId: Int = checkNotNull(savedStateHandle[ToDoDetailsDestination.todoIdArg])

    val uiState: StateFlow<ToDoUiState> = toDoRepository.getToDoStream(id = todoId)
        .filterNotNull()
        .map {
            it.toToDoUiState()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ToDoUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}