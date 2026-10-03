package com.example.schedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.schedule.ScheduleApplication
import com.example.schedule.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    
    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: return
        
        NotificationHelper.createNotificationChannel(context)
        
        val repository = (context.applicationContext as ScheduleApplication).taskRepository
        
        CoroutineScope(Dispatchers.IO).launch {
            val task = repository.getTaskById(taskId)
            if (task != null && task.status == com.example.schedule.data.model.TaskStatus.PENDING) {
                if (task.reminderType == com.example.schedule.data.model.ReminderType.ALARM) {
                    val serviceIntent = Intent(context, com.example.schedule.service.AlarmService::class.java).apply {
                        putExtra(com.example.schedule.service.AlarmService.EXTRA_TASK_ID, task.id)
                    }
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceIntent)
                    } else {
                        context.startService(serviceIntent)
                    }
                } else {
                    NotificationHelper.showTaskNotification(context, task)
                }
            }
        }
    }
}
