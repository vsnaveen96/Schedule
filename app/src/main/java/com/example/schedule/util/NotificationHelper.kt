package com.example.schedule.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.schedule.MainActivity
import com.example.schedule.data.model.TaskEntity
import com.example.schedule.receiver.ActionReceiver

object NotificationHelper {
    private const val CHANNEL_ID = "task_reminders_channel"
    private const val CHANNEL_NAME = "Task Reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for task reminders"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showTaskNotification(context: Context, task: TaskEntity) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent to open app
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            task.id.hashCode(),
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Done Action
        val doneIntent = Intent(context, ActionReceiver::class.java).apply {
            action = ActionReceiver.ACTION_DONE
            putExtra(ActionReceiver.EXTRA_TASK_ID, task.id)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode() + 1,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Snooze Action (15 mins)
        val snoozeIntent = Intent(context, ActionReceiver::class.java).apply {
            action = ActionReceiver.ACTION_SNOOZE
            putExtra(ActionReceiver.EXTRA_TASK_ID, task.id)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode() + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(task.title)
            .setContentText(task.description.ifBlank { "Task is due now!" })
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setOngoing(true) // Task Acknowledgement: Requires user action
            .addAction(android.R.drawable.ic_menu_edit, "Done", donePendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, "Snooze (15m)", snoozePendingIntent)

        notificationManager.notify(task.id.hashCode(), builder.build())
    }
    
    fun cancelNotification(context: Context, taskId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId.hashCode())
    }
}
