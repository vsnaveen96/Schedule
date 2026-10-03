package com.example.schedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.schedule.ScheduleApplication
import com.example.schedule.data.model.TaskStatus
import com.example.schedule.util.NotificationHelper
import com.example.schedule.util.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DONE = "com.example.schedule.ACTION_DONE"
        const val ACTION_SNOOZE = "com.example.schedule.ACTION_SNOOZE"
        const val EXTRA_TASK_ID = "extra_task_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(EXTRA_TASK_ID) ?: return
        val action = intent.action ?: return
        
        val repository = (context.applicationContext as ScheduleApplication).taskRepository

        CoroutineScope(Dispatchers.IO).launch {
            val task = repository.getTaskById(taskId) ?: return@launch

            when (action) {
                ACTION_DONE -> {
                    // Update task to DONE
                    repository.updateTask(task.copy(
                        status = TaskStatus.DONE,
                        lastModifiedAt = System.currentTimeMillis()
                    ))
                    NotificationHelper.cancelNotification(context, taskId)
                    
                    // Note: If recurring, next generation logic should ideally be handled by domain layer.
                    // For M7, we just update status and cancel notification. M6 handles recurring in ViewModel,
                    // we could abstract that to repository.
                }
                ACTION_SNOOZE -> {
                    // Snooze by 15 minutes
                    val snoozeTime = System.currentTimeMillis() + (15 * 60 * 1000)
                    val updatedTask = task.copy(
                        dueTime = snoozeTime,
                        // If it only had a dueDate, we should probably set dueTime now
                        lastModifiedAt = System.currentTimeMillis()
                    )
                    repository.updateTask(updatedTask)
                    NotificationHelper.cancelNotification(context, taskId)
                    ReminderManager.scheduleReminder(context, updatedTask)
                }
            }
        }
    }
}
