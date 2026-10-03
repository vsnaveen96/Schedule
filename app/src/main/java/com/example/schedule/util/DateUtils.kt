package com.example.schedule.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    fun formatTime(timeInMillis: Long?): String {
        if (timeInMillis == null) return ""
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        return formatter.format(Date(timeInMillis))
    }

    fun formatDate(dateInMillis: Long?): String {
        if (dateInMillis == null) return ""
        val formatter = SimpleDateFormat("MMM dd", Locale.getDefault())
        return formatter.format(Date(dateInMillis))
    }

    fun isOverdue(dueDate: Long?, dueTime: Long?): Boolean {
        if (dueDate == null) return false
        val now = Calendar.getInstance()
        
        val target = Calendar.getInstance().apply {
            timeInMillis = dueDate
            if (dueTime != null) {
                val timeCal = Calendar.getInstance().apply { timeInMillis = dueTime }
                set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
            } else {
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
            }
        }
        
        return target.before(now)
    }

    fun isToday(dateInMillis: Long?): Boolean {
        if (dateInMillis == null) return false
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = dateInMillis }
        return now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
               now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }
}
