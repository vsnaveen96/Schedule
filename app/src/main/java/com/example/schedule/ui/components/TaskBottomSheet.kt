package com.example.schedule.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.schedule.data.model.Priority
import com.example.schedule.data.model.ReminderType
import com.example.schedule.data.model.RepeatType
import com.example.schedule.data.model.SyncStatus
import com.example.schedule.data.model.TaskEntity
import com.example.schedule.data.model.TaskStatus
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    taskToEdit: TaskEntity? = null,
    onDismissRequest: () -> Unit,
    onSaveTask: (TaskEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    var title by remember { mutableStateOf(taskToEdit?.title ?: "") }
    var description by remember { mutableStateOf(taskToEdit?.description ?: "") }
    var repeatType by remember { mutableStateOf(taskToEdit?.repeatType ?: RepeatType.NONE) }
    var reminderType by remember { mutableStateOf(taskToEdit?.reminderType ?: ReminderType.NOTIFICATION) }
    var expandedRepeat by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 32.dp) // extra padding for nav bar area
        ) {
            Text(
                text = if (taskToEdit == null) "New Task" else "Edit Task",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Minimal dropdown for RepeatType
            Box {
                Button(
                    onClick = { expandedRepeat = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = "Repeat: ${repeatType.name}")
                }
                
                androidx.compose.material3.DropdownMenu(
                    expanded = expandedRepeat,
                    onDismissRequest = { expandedRepeat = false }
                ) {
                    RepeatType.values().forEach { type ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                repeatType = type
                                expandedRepeat = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reminder Type Dropdown
            var expandedReminder by remember { mutableStateOf(false) }
            Box {
                Button(
                    onClick = { expandedReminder = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(text = "Reminder: ${reminderType.name}")
                }
                
                androidx.compose.material3.DropdownMenu(
                    expanded = expandedReminder,
                    onDismissRequest = { expandedReminder = false }
                ) {
                    ReminderType.values().forEach { type ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                reminderType = type
                                expandedReminder = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val task = taskToEdit?.copy(
                            title = title,
                            description = description,
                            repeatType = repeatType,
                            reminderType = reminderType,
                            lastModifiedAt = System.currentTimeMillis()
                        ) ?: TaskEntity(
                            id = UUID.randomUUID().toString(),
                            title = title,
                            description = description,
                            categoryId = "default",
                            priority = Priority.MEDIUM,
                            repeatType = repeatType,
                            reminderType = reminderType,
                            status = TaskStatus.PENDING,
                            createdAt = System.currentTimeMillis(),
                            lastModifiedAt = System.currentTimeMillis(),
                            syncStatus = SyncStatus.PENDING_SYNC
                        )
                        onSaveTask(task)
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Save Task",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
