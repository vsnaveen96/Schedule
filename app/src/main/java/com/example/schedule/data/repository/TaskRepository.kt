package com.example.schedule.data.repository

import com.example.schedule.data.model.CategoryEntity
import com.example.schedule.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    // Tasks
    fun getAllActiveTasks(): Flow<List<TaskEntity>>
    suspend fun getTaskById(taskId: String): TaskEntity?
    suspend fun insertTask(task: TaskEntity)
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(taskId: String)
    
    // Categories
    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun insertCategory(category: CategoryEntity)
    suspend fun insertCategories(categories: List<CategoryEntity>)
}
