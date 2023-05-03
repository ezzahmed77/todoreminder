import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.android.mytodo.data.model.Todo
import com.example.android.mytodo.utils.NotificationPublisher
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

@RequiresApi(Build.VERSION_CODES.S)
fun scheduleNotification(
    context: Context,
    todo: Todo,
    notificationId: Int
) {
    // Get the AlarmManager service
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Create a PendingIntent for the notification
    val intent = Intent(context, NotificationPublisher::class.java).apply {
        putExtra("title", todo.title)
        putExtra("description", todo.description)
        putExtra("notificationId", notificationId)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    val dueDateTime = LocalDateTime.of(todo.date, todo.time )
    val zonedDateTime: ZonedDateTime = dueDateTime.atZone(ZoneId.systemDefault()) // convert to ZonedDateTime
    val timeInMilliSeconds: Long = zonedDateTime.toInstant().toEpochMilli()
    // Calculate the trigger time
    val calendar = Calendar.getInstance().apply {
        timeInMillis =timeInMilliSeconds
    }

    // Schedule the notification using the AlarmManager
    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

fun cancelNotification(context: Context, todoId: Int) {
    val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java) as NotificationManager
    notificationManager.cancel(todoId)
}

