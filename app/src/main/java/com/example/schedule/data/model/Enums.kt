package com.example.schedule.data.model

enum class Priority {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum class RepeatType {
    NONE, DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
}

enum class ReminderType {
    NOTIFICATION, ALARM
}

enum class TaskStatus {
    PENDING, DONE, SNOOZED, SKIPPED
}

enum class SyncStatus {
    SYNCED, PENDING_SYNC
}
