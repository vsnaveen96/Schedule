package com.example.schedule.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val repeatType: RepeatType = RepeatType.NONE,
    val reminderType: ReminderType = ReminderType.NOTIFICATION,
    val priority: Priority = Priority.LOW,
    val categoryId: String,
    val status: TaskStatus = TaskStatus.PENDING,
    val isArchived: Boolean = false,
    val createdAt: Long,
    val lastModifiedAt: Long,
    val syncStatus: SyncStatus = SyncStatus.PENDING_SYNC
)
