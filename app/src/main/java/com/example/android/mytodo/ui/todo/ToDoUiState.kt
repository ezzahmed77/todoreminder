package com.example.android.mytodo.ui.todo

import com.example.android.mytodo.data.Priority
import com.example.android.mytodo.data.ToDo
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents Ui State for an Item.
 */
data class ToDoUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.NotSelected,
    val hasDateAndTime: Boolean = false,
    val date: LocalDate  = LocalDate.now(),
    val time: LocalTime  = LocalTime.now(),
    val hasReminder: Boolean = false,
    val isCompleted: Boolean = false,
    val isTrashed: Boolean = false,
    val actionEnabled: Boolean = false
)

/**
 * Extension function to convert [ToDoUiState] to [ToDo].
 */
fun ToDoUiState.toToDo(): ToDo = ToDo(
    id = id,
    title = title ?: "No Title",
    description = description,
    priority = priority,
    hasDateAndTime = hasDateAndTime,
    date = date,
    time = time,
    hasReminder = hasReminder,
    isCompleted = isCompleted,
    isTrashed = isTrashed
)

/**
 * Extension function to convert [ToDo] to [ToDoUiState]
 */
fun ToDo.toToDoUiState(actionEnabled: Boolean = false): ToDoUiState = ToDoUiState(
    id = id,
    title = title,
    description = description,
    priority = priority,
    hasDateAndTime = hasDateAndTime,
    date = date,
    time = time,
    hasReminder = hasReminder,
    isCompleted = isCompleted,
    isTrashed = isTrashed,
    actionEnabled = actionEnabled
)

fun ToDoUiState.isValid() : Boolean {
    return title.isNotBlank()
}
