package com.example.schedule.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.schedule.ScheduleApplication
import com.example.schedule.util.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val repository = (context.applicationContext as ScheduleApplication).taskRepository
            
            CoroutineScope(Dispatchers.IO).launch {
                val activeTasks = repository.getAllActiveTasks().first()
                activeTasks.forEach { task ->
                    ReminderManager.scheduleReminder(context, task)
                }
            }
        }
    }
}
