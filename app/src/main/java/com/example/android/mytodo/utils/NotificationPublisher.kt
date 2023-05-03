package com.example.android.mytodo.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.android.mytodo.R

// Define a BroadcastReceiver to publish the notification
class NotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Get the notification details from the intent
        val title = intent?.getStringExtra("title")
        val description = intent?.getStringExtra("description")
        val notificationId = intent?.getIntExtra("notificationId", 0)

        // Create a notification builder
        val builder = NotificationCompat.Builder(context!!, "default")
            .setSmallIcon(R.drawable.bell_on)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Create a notification channel for devices running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "Todo Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channel.id)
        }

        // Publish the notification
        val notification = builder.build()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId!!, notification)
    }
}
