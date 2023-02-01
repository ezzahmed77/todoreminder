package com.example.android.mytodo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.android.mytodo.R
import com.example.android.mytodo.ui.todo.ToDoUiState

const val CHANNEL_ID = "todo_task_reminder"

fun createNotificationChannel(channelId: String = CHANNEL_ID, context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "MyReminderChannel"
        val descriptionText = "My todo reminder channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


// shows a simple notification with a tap action to show an activity
fun showSimpleNotification(
    context: Context,
    channelId: String = CHANNEL_ID,
    notificationId: Int,
    title: String? = "No Title",
    description: String? = "No Description",
    priority: Int = NotificationCompat.PRIORITY_DEFAULT
) {
    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(priority)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}