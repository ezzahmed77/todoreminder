package com.example.android.mytodo.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.android.mytodo.ToDoApplication
import com.example.android.mytodo.ui.home.HomeViewModel
import com.example.android.mytodo.ui.todo.TodoDetailViewModel
import com.example.android.mytodo.ui.todo.TodoEditViewModel
import com.example.android.mytodo.ui.todo.TodoEntryViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                context = todoApplication().applicationContext,
                todoRepository = todoApplication().container.todoRepository
            )
        }

        // Initializer for ToDoDetailViewModel
        initializer {
            TodoDetailViewModel(
                this.createSavedStateHandle(),
                todoApplication().container.todoRepository
            )
        }

        // Initializer for ToDoEntryViewModel
        initializer {
            TodoEntryViewModel(
                context = todoApplication().applicationContext,
                todoRepository =  todoApplication().container.todoRepository,
            )
        }

        // Initializer for ToDoDetailViewModel
        initializer {
            TodoEditViewModel(
                todoApplication().applicationContext,
                this.createSavedStateHandle(),
                todoApplication().container.todoRepository
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
