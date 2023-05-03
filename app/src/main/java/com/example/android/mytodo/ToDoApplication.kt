package com.example.android.mytodo

import android.app.Application
import com.example.android.mytodo.data.AppContainer
import com.example.android.mytodo.data.AppDataContainer

class ToDoApplication: Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}