package com.example.android.mytodo.ui.home

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
import com.example.android.mytodo.ui.todo.FilterType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import scheduleNotification


class HomeViewModel(
    val context: Context,
    private val todoRepository: TodoRepository
): ViewModel() {

    var homeUiState: StateFlow<HomeUiState> = todoRepository.getAllTodosStream().map{todos->
        HomeUiState(todos)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )

    var filter by mutableStateOf(FilterType.ALL)
    private set

    var sort by mutableStateOf(Sort.TITLE)
    private set

    fun updateFilter(filterType: FilterType){
        this@HomeViewModel.filter = filterType
    }
    fun updateSort(sort: Sort){
        this@HomeViewModel.sort = sort
    }


    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun updateTodo(newTodo: Todo) {
        todoRepository.updateTodo(newTodo)
        if(newTodo.isTrashed){
            updateReminder(newTodo)
        }else{
            updateReminder(newTodo)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    suspend fun deleteTodo(todo: Todo){
        todoRepository.deleteTodo(todo)
        updateReminder(todo)
    }

    suspend fun deleteAllTodosInTrash(){
        homeUiState.value.todos.filter { it.isTrashed }
            .forEach {
                todoRepository.deleteTodo(it)
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
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class HomeUiState(val todos: List<Todo> = listOf())