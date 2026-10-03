package com.example.schedule

import android.app.Application
import com.example.schedule.data.local.ScheduleDatabase
import com.example.schedule.data.repository.OfflineFirstTaskRepository
import com.example.schedule.data.repository.TaskRepository

class ScheduleApplication : Application() {

    lateinit var database: ScheduleDatabase
        private set

    lateinit var taskRepository: TaskRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = ScheduleDatabase.getDatabase(this)
        taskRepository = OfflineFirstTaskRepository(database.taskDao(), database.categoryDao())
    }
}
