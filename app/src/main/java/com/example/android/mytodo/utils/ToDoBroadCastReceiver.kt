package com.example.android.mytodo.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.mytodo.ui.todo.TODO_DESCRIPTION
import com.example.android.mytodo.ui.todo.TODO_TITLE

class ToDoBroadCastReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("AlarmUpdated", "Received successfully!!!!")
        val title = intent?.getStringExtra(TODO_TITLE)
        val description = intent?.getStringExtra(TODO_DESCRIPTION)
        showSimpleNotification(
            context = context!!,
            notificationId = 200,
            title = title,
            description = description
        )
    }

}
