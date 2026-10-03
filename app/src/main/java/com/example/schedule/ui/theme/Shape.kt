package com.example.schedule.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp), // buttons/chips
    medium = RoundedCornerShape(16.dp), // cards/sheets
    large = RoundedCornerShape(50) // full round FAB (fallback to 50% / CircleShape usually handled directly)
)

// Spacing Tokens (4dp base grid)
object Spacing {
    val Base = 4.dp
    val Small = 8.dp
    val Medium = 16.dp
    val Large = 24.dp
}
