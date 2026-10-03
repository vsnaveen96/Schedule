package com.example.schedule.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.schedule.ScheduleApplication
import com.example.schedule.ui.TasksViewModel
import com.example.schedule.ui.TasksViewModelFactory
import com.example.schedule.ui.components.TaskRow
import com.example.schedule.util.DateUtils
import com.example.schedule.data.model.TaskEntity
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodayScreen(
    contentPadding: PaddingValues, 
    onEditTask: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val application = context.applicationContext as ScheduleApplication
    val viewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory(application, application.taskRepository)
    )
    
    val tasks by viewModel.tasks.collectAsState()
    
    // Group tasks
    val overdue = tasks.filter { DateUtils.isOverdue(it.dueDate, it.dueTime) && !DateUtils.isToday(it.dueDate) }
    val today = tasks.filter { DateUtils.isToday(it.dueDate) || (it.dueDate == null) }
    val upcoming = tasks.filter { !DateUtils.isOverdue(it.dueDate, it.dueTime) && !DateUtils.isToday(it.dueDate) && it.dueDate != null }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        if (overdue.isNotEmpty()) {
            stickyHeader { SectionHeader("Overdue") }
            items(overdue) { task ->
                TaskRow(
                    task = task, 
                    category = null, 
                    onClick = { onEditTask(task) },
                    onStatusChange = { newStatus -> viewModel.updateTask(task.copy(status = newStatus)) }
                )
            }
        }
        
        if (today.isNotEmpty()) {
            stickyHeader { SectionHeader("Today") }
            items(today) { task ->
                TaskRow(
                    task = task, 
                    category = null, 
                    onClick = { onEditTask(task) },
                    onStatusChange = { newStatus -> viewModel.updateTask(task.copy(status = newStatus)) }
                )
            }
        }
        
        if (upcoming.isNotEmpty()) {
            stickyHeader { SectionHeader("Upcoming") }
            items(upcoming) { task ->
                TaskRow(
                    task = task, 
                    category = null, 
                    onClick = { onEditTask(task) },
                    onStatusChange = { newStatus -> viewModel.updateTask(task.copy(status = newStatus)) }
                )
            }
        }
        
        if (tasks.isEmpty()) {
            item {
                Text(
                    text = "No tasks yet. Enjoy your day!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .fillParentMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun CalendarScreen(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
        Text("Calendar View")
    }
}

@Composable
fun TasksScreen(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
        Text("Tasks View")
    }
}

@Composable
fun SettingsScreen(contentPadding: PaddingValues, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
        Text("Settings View")
    }
}
