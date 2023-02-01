package com.example.android.mytodo.data

import android.content.Context


/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val toDoRepository: ToDoRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineToDoRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ToDoRepository]
     */
    override val toDoRepository: ToDoRepository by lazy {

        OfflineToDoRepository(ToDoDatabase.getDatabase(context).toDoDao())
    }
}