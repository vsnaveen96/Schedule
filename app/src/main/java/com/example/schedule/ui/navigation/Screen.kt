package com.example.schedule.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Today : Screen("today", "Today")
    object Calendar : Screen("calendar", "Calendar")
    object Tasks : Screen("tasks", "Tasks")
    object Settings : Screen("settings", "Settings")
}

val BottomNavItems = listOf(
    Screen.Today,
    Screen.Calendar,
    Screen.Tasks,
    Screen.Settings
)
