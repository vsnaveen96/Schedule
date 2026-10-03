package com.example.schedule.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.schedule.data.model.TaskEntity
import com.example.schedule.receiver.ReminderReceiver
import java.util.Calendar

object ReminderManager {

    fun scheduleReminder(context: Context, task: TaskEntity) {
        if (task.dueDate == null) return
        
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_TASK_ID, task.id)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = Calendar.getInstance().apply {
            timeInMillis = task.dueDate
            if (task.dueTime != null) {
                val timeCal = Calendar.getInstance().apply { timeInMillis = task.dueTime }
                set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
            } else {
                set(Calendar.HOUR_OF_DAY, 9) // Default to 9 AM if only date is set
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
        }
        
        // Don't schedule in the past
        if (calendar.timeInMillis < System.currentTimeMillis()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback or request permission - handled dynamically in a real app
            // For now, try to schedule inexactly if permission denied
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            return
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, taskId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        NotificationHelper.cancelNotification(context, taskId)
    }
}
