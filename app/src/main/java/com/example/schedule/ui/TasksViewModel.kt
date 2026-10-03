package com.example.schedule.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.schedule.data.model.TaskEntity
import com.example.schedule.data.repository.TaskRepository
import com.example.schedule.util.ReminderManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(
    private val application: Application,
    private val repository: TaskRepository
) : AndroidViewModel(application) {

    val tasks: StateFlow<List<TaskEntity>> = repository.getAllActiveTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insertTask(task)
            ReminderManager.scheduleReminder(application, task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            
            if (task.status == com.example.schedule.data.model.TaskStatus.DONE) {
                ReminderManager.cancelReminder(application, task.id)
                if (task.repeatType != com.example.schedule.data.model.RepeatType.NONE) {
                    generateNextRecurringTask(task)
                }
            } else {
                ReminderManager.scheduleReminder(application, task)
            }
        }
    }
    
    private suspend fun generateNextRecurringTask(completedTask: TaskEntity) {
        val calendar = java.util.Calendar.getInstance()
        if (completedTask.dueDate != null) {
            calendar.timeInMillis = completedTask.dueDate
        }
        
        when (completedTask.repeatType) {
            com.example.schedule.data.model.RepeatType.DAILY -> calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
            com.example.schedule.data.model.RepeatType.WEEKLY -> calendar.add(java.util.Calendar.WEEK_OF_YEAR, 1)
            com.example.schedule.data.model.RepeatType.MONTHLY -> calendar.add(java.util.Calendar.MONTH, 1)
            com.example.schedule.data.model.RepeatType.YEARLY -> calendar.add(java.util.Calendar.YEAR, 1)
            else -> return
        }
        
        val nextTask = completedTask.copy(
            id = java.util.UUID.randomUUID().toString(),
            dueDate = calendar.timeInMillis,
            status = com.example.schedule.data.model.TaskStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            lastModifiedAt = System.currentTimeMillis(),
            syncStatus = com.example.schedule.data.model.SyncStatus.PENDING_SYNC
        )
        repository.insertTask(nextTask)
        ReminderManager.scheduleReminder(application, nextTask)
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
            ReminderManager.cancelReminder(application, taskId)
        }
    }
}

class TasksViewModelFactory(
    private val application: Application,
    private val repository: TaskRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
