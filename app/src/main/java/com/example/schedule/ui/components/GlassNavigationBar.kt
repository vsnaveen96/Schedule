package com.example.schedule.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.schedule.ui.navigation.BottomNavItems
import com.example.schedule.ui.navigation.Screen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.HazeStyle

@Composable
fun GlassNavigationBar(
    navController: NavController,
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Define glass modifier
    val glassModifier = modifier
        .hazeChild(
            state = hazeState, 
            style = HazeStyle(
                tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                blurRadius = 24.dp
            )
        )
        .border(1.dp, Color.White.copy(alpha = 0.15f))

    NavigationBar(
        modifier = glassModifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        BottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    val icon = when (screen) {
                        Screen.Today -> Icons.Outlined.WbSunny
                        Screen.Calendar -> Icons.Outlined.CalendarToday
                        Screen.Tasks -> Icons.Outlined.CheckCircle
                        Screen.Settings -> Icons.Outlined.Settings
                        else -> Icons.Outlined.CheckCircle
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title, style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}
