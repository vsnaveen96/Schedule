package com.example.schedule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.schedule.data.model.CategoryEntity
import com.example.schedule.data.model.TaskEntity
import com.example.schedule.ui.theme.Spacing
import com.example.schedule.util.DateUtils
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle

import com.example.schedule.data.model.TaskStatus

@Composable
fun TaskRow(
    task: TaskEntity,
    category: CategoryEntity?,
    onClick: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryBg = category?.colorHex?.let { Color(android.graphics.Color.parseColor(it)) } 
        ?: MaterialTheme.colorScheme.surfaceVariant
    val categoryText = MaterialTheme.colorScheme.onSurfaceVariant 

    Column(modifier = modifier.clickable { onClick() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox for Task Completion
            androidx.compose.material3.Checkbox(
                checked = task.status == TaskStatus.DONE,
                onCheckedChange = { isChecked ->
                    val newStatus = if (isChecked) TaskStatus.DONE else TaskStatus.PENDING
                    onStatusChange(newStatus)
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Category Icon (Rounded Square)
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(categoryBg.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle, 
                    contentDescription = null,
                    tint = categoryBg,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Metadata
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val metaText = buildString {
                    if (task.dueTime != null) append(DateUtils.formatTime(task.dueTime))
                    if (task.dueTime != null && category != null) append(" • ")
                    if (category != null) append(category.name)
                }
                
                if (metaText.isNotEmpty()) {
                    Text(
                        text = metaText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right-aligned Date/Time
            Column(horizontalAlignment = Alignment.End) {
                val timeStr = DateUtils.formatTime(task.dueTime)
                val dateStr = DateUtils.formatDate(task.dueDate)
                
                if (timeStr.isNotEmpty()) {
                    Text(
                        text = timeStr,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (dateStr.isNotEmpty()) {
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // Hairline Divider
        Divider(
            modifier = Modifier.padding(start = 72.dp), // align with text
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    }
}
