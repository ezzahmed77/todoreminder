package com.example.android.mytodo.ui.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.mytodo.data.ToDoRepository
import com.example.android.mytodo.utils.ToDoBroadCastReceiver
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ToDoEditViewModel(
    val context: Context,
    savedStateHandle: SavedStateHandle,
    private val toDoRepository: ToDoRepository
) : ViewModel() {

    /**
     * Holds current todoItem ui state
     */
    var toDoUiState by mutableStateOf(ToDoUiState())
        private set

    private val todoId: Int = checkNotNull(savedStateHandle[ToDoEditDestination.toDoIdArg])

    /**
     * Getting the UiState
     */
    init {
        viewModelScope.launch {
            toDoUiState = toDoRepository.getToDoStream(id = todoId)
                .filterNotNull()
                .first()
                .toToDoUiState(actionEnabled = true)
        }
    }

    fun updateUiState(newToDoUiState: ToDoUiState) {
        toDoUiState = newToDoUiState.copy( actionEnabled = newToDoUiState.isValid())
    }

    suspend fun updateToDo() {
        if (toDoUiState.isValid()) {
            toDoRepository.updateToDo(toDoUiState.toToDo())
            updateReminder()
        }
    }


    private fun updateReminder() {
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

        if(toDoUiState.hasReminder){
            alarmMgr.setExact(
                AlarmManager.RTC_WAKEUP,
                localDateTime,
                alarmIntent
            )
        }else{
            alarmMgr.cancel(alarmIntent)
        }

    }


}
