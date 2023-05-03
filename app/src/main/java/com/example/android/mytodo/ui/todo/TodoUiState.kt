package com.example.android.mytodo.ui.todo

import com.example.android.mytodo.data.model.Priority
import com.example.android.mytodo.data.model.Todo
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents Ui State for an Item.
 */


data class TodoUiState(
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
 * Extension function to convert [TodoUiState] to [Todo].
 */
fun TodoUiState.toTodo(): Todo = Todo(
    id = id,
    title = title,
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
 * Extension function to convert [Todo] to [TodoUiState]
 */
fun Todo.toTodoUiState(actionEnabled: Boolean = false): TodoUiState = TodoUiState(
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

fun TodoUiState.isValid() : Boolean {
    return title.isNotBlank()
}
