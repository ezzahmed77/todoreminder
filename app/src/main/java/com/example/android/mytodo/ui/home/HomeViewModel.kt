package com.example.android.mytodo.ui.home

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.mytodo.data.Priority
import com.example.android.mytodo.data.ToDoRepository
import com.example.android.mytodo.ui.todo.*
import com.example.android.mytodo.utils.ToDoBroadCastReceiver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
enum class Case{
    Update, Delete
}
class HomeViewModel(
    val context: Context,
    private val todoRepository: ToDoRepository): ViewModel() {

    var homeUiState: StateFlow<HomeUiState> = todoRepository.getAllTodosStream().map{
        HomeUiState(it.map{it.toToDoUiState()})
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = HomeUiState()
    )

    var filter by mutableStateOf(FilterType.ALL)
    private set

    fun updateFilter(filterType: FilterType){
        filter = filterType
    }

    var sort by mutableStateOf(Sort.TITLE)
    private set

    fun updateSort(sort: Sort){
        this@HomeViewModel.sort = sort
    }


    suspend fun updateToDo(newToDoUiState: ToDoUiState) {
        todoRepository.updateToDo(newToDoUiState.toToDo())
        if(newToDoUiState.isTrashed){
            updateReminder(newToDoUiState, Case.Delete)
        }else{
            updateReminder(newToDoUiState, Case.Update)
        }
    }

    suspend fun deleteToDo(toDoUiState: ToDoUiState){
        todoRepository.deleteToDo(toDoUiState.toToDo())
        updateReminder(toDoUiState, Case.Delete)
    }

    suspend fun deleteAllToDosInTrash(){
        homeUiState.value.todosUiState.filter { it.isTrashed }
            .forEach {
                todoRepository.deleteToDo(it.toToDo())
            }
    }

    private fun updateReminder(toDoUiState: ToDoUiState, case: Case) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ToDoBroadCastReceiver::class.java)
            .putExtra(TODO_TITLE, toDoUiState.title)
            .putExtra(TODO_DESCRIPTION, toDoUiState.description)
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val localDateTime = LocalDateTime.of(toDoUiState.date, toDoUiState.time).toInstant(
            ZonedDateTime.now().offset).toEpochMilli()

        if(case == Case.Update){
            if(toDoUiState.hasReminder){
                alarmMgr.setExact(
                    AlarmManager.RTC_WAKEUP,
                    localDateTime,
                    alarmIntent
                )
            }else{
                alarmMgr.cancel(alarmIntent)
            }
        }else {
            alarmMgr.cancel(alarmIntent)
        }


    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class HomeUiState(val todosUiState: List<ToDoUiState> = listOf())