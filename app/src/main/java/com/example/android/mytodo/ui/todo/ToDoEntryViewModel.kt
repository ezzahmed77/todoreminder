package com.example.android.mytodo.ui.todo

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.mytodo.MainActivity
import com.example.android.mytodo.ToDoApp
import com.example.android.mytodo.ToDoApplication
import com.example.android.mytodo.data.ToDoRepository
import com.example.android.mytodo.utils.ToDoBroadCastReceiver
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.days

const val TODO_TITLE = "todo_title"
const val TODO_DESCRIPTION = "todo_description"
class ToDoEntryViewModel(
    private val  context: Context,
    private val toDoRepository: ToDoRepository,
    private val alarmManager: AlarmManager
): ViewModel() {

    /**
     * Holds current todoItem ui state
     */
    var todoUiState by mutableStateOf(ToDoUiState())
        private set

    /**
     * Updates the [todoUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(newToDoUiState: ToDoUiState) {
        todoUiState = newToDoUiState.copy( actionEnabled = newToDoUiState.isValid())
    }
    /**
     * Saving item to database
     */
    suspend fun saveToDo() {
        if(todoUiState.isValid()){
            viewModelScope.launch {
                toDoRepository.insertToDo(todoUiState.toToDo())
                updateReminder()
            }
        }
    }
    /**
     * Set reminder with alarm manager
     */


    private fun updateReminder() {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ToDoBroadCastReceiver::class.java)
            .putExtra(TODO_TITLE, todoUiState.title)
            .putExtra(TODO_DESCRIPTION, todoUiState.description)
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val localDateTime = LocalDateTime.of(todoUiState.date, todoUiState.time).toInstant(
            ZonedDateTime.now().offset).toEpochMilli()

        if(todoUiState.hasReminder){
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