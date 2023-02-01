package com.example.android.mytodo

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import com.example.android.mytodo.data.AppContainer
import com.example.android.mytodo.data.AppDataContainer
import com.example.android.mytodo.utils.createNotificationChannel

class ToDoApplication(): Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer
    lateinit var context: Context
    lateinit var alarmManager: AlarmManager
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
        context = this
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        createNotificationChannel(context = this)
    }
}