package com.example.schedule.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.schedule.ScheduleApplication
import com.example.schedule.data.model.TaskEntity
import com.example.schedule.ui.components.GlassNavigationBar
import com.example.schedule.ui.components.GlassTopAppBar
import com.example.schedule.ui.components.TaskBottomSheet
import com.example.schedule.ui.navigation.Screen
import com.example.schedule.ui.screens.CalendarScreen
import com.example.schedule.ui.screens.SettingsScreen
import com.example.schedule.ui.screens.TasksScreen
import com.example.schedule.ui.screens.TodayScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze

@Composable
fun ScheduleAppShell() {
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Today.route

    var showBottomSheet by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<TaskEntity?>(null) }

    val context = LocalContext.current
    val application = context.applicationContext as ScheduleApplication
    val tasksViewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory(application, application.taskRepository)
    )

    val title = when (currentRoute) {
        Screen.Today.route -> Screen.Today.title
        Screen.Calendar.route -> Screen.Calendar.title
        Screen.Tasks.route -> Screen.Tasks.title
        Screen.Settings.route -> Screen.Settings.title
        else -> "Schedule"
    }

    if (showBottomSheet || taskToEdit != null) {
        TaskBottomSheet(
            taskToEdit = taskToEdit,
            onDismissRequest = { 
                showBottomSheet = false
                taskToEdit = null
            },
            onSaveTask = { task -> 
                if (taskToEdit == null) {
                    tasksViewModel.insertTask(task)
                } else {
                    tasksViewModel.updateTask(task)
                }
                showBottomSheet = false
                taskToEdit = null
            }
        )
    }

    Scaffold(
        topBar = {
            GlassTopAppBar(
                title = title,
                hazeState = hazeState
            )
        },
        bottomBar = {
            GlassNavigationBar(
                navController = navController,
                hazeState = hazeState
            )
        },
        floatingActionButton = {
            if (currentRoute == Screen.Today.route || currentRoute == Screen.Tasks.route) {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Add Task")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Today.route,
            modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)
        ) {
            composable(Screen.Today.route) {
                TodayScreen(
                    contentPadding = innerPadding,
                    onEditTask = { task -> taskToEdit = task }
                )
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(contentPadding = innerPadding)
            }
            composable(Screen.Tasks.route) {
                TasksScreen(contentPadding = innerPadding)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(contentPadding = innerPadding)
            }
        }
    }
}
