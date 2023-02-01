package com.example.android.mytodo.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.android.mytodo.ToDoApplication
import com.example.android.mytodo.ui.home.HomeViewModel
import com.example.android.mytodo.ui.todo.ToDoDetailViewModel
import com.example.android.mytodo.ui.todo.ToDoEditViewModel
import com.example.android.mytodo.ui.todo.ToDoEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                context = todoApplication().context,
                todoRepository = todoApplication().container.toDoRepository
            )
        }

        // Initializer for ToDoDetailViewModel
        initializer {
            ToDoDetailViewModel(
                this.createSavedStateHandle(),
                todoApplication().container.toDoRepository
            )
        }

        // Initializer for ToDoEntryViewModel
        initializer {
            ToDoEntryViewModel(
                context = todoApplication().context,
                toDoRepository =  todoApplication().container.toDoRepository,
                alarmManager = todoApplication().alarmManager
            )
        }

        // Initializer for ToDoDetailViewModel
        initializer {
            ToDoEditViewModel(
                todoApplication().context,
                this.createSavedStateHandle(),
                todoApplication().container.toDoRepository
            )
        }

    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ToDoApplication].
 */
fun CreationExtras.todoApplication(): ToDoApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ToDoApplication)
