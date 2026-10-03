package com.example.schedule.ui.screens

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.schedule.ScheduleApplication
import com.example.schedule.data.model.TaskEntity
import com.example.schedule.data.model.TaskStatus
import com.example.schedule.service.AlarmService
import com.example.schedule.ui.theme.ScheduleTheme
import com.example.schedule.util.ReminderManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()

        val taskId = intent.getStringExtra(AlarmService.EXTRA_TASK_ID) ?: return finish()

        setContent {
            ScheduleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AlarmScreen(
                        taskId = taskId,
                        onDismiss = { stopAlarmAndFinish() },
                        application = application as ScheduleApplication
                    )
                }
            }
        }
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    private fun stopAlarmAndFinish() {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP
        }
        startService(intent)
        finish()
    }
}

@Composable
fun AlarmScreen(
    taskId: String,
    onDismiss: () -> Unit,
    application: ScheduleApplication
) {
    var task by remember { mutableStateOf<TaskEntity?>(null) }
    val repository = application.taskRepository

    LaunchedEffect(taskId) {
        withContext(Dispatchers.IO) {
            task = repository.getTaskById(taskId)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (task != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TASK ALARM",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = task!!.title,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                if (task!!.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = task!!.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(64.dp))

                // Action Buttons
                Button(
                    onClick = {
                        // Mark Done
                        val updated = task!!.copy(
                            status = TaskStatus.DONE,
                            lastModifiedAt = System.currentTimeMillis()
                        )
                        kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                            repository.updateTask(updated)
                            // Note: Next recurring task generation omitted for brevity in AlarmActivity, 
                            // typically it should be handled centrally by a UseCase
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("MARK AS DONE", style = MaterialTheme.typography.titleLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            // Snooze 15 mins
                            val snoozeTime = System.currentTimeMillis() + (15 * 60 * 1000)
                            val updated = task!!.copy(
                                dueTime = snoozeTime,
                                lastModifiedAt = System.currentTimeMillis()
                            )
                            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                repository.updateTask(updated)
                            }
                            ReminderManager.scheduleReminder(application, updated)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("SNOOZE 15M", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(
                        onClick = {
                            // Skip
                            val updated = task!!.copy(
                                status = TaskStatus.SKIPPED,
                                lastModifiedAt = System.currentTimeMillis()
                            )
                            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                                repository.updateTask(updated)
                            }
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("SKIP", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
