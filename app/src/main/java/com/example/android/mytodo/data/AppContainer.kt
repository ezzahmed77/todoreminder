package com.example.android.mytodo.data

import android.content.Context
import com.example.android.mytodo.data.repositories.TodoRepositoryImpl
import com.example.android.mytodo.data.repositories.TodoRepository


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val todoRepository: TodoRepository
}

/**
 * [AppContainer] implementation that provides instance of [TodoRepositoryImpl]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [TodoRepository]
     */
    override val todoRepository: TodoRepository by lazy {

        TodoRepositoryImpl(TodoDatabase.getDatabase(context).todoDao())
    }
}