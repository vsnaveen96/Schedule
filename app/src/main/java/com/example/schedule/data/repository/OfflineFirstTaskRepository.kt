package com.example.schedule.data.repository

import com.example.schedule.data.local.CategoryDao
import com.example.schedule.data.local.TaskDao
import com.example.schedule.data.model.CategoryEntity
import com.example.schedule.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

class OfflineFirstTaskRepository(
    private val taskDao: TaskDao,
    private val categoryDao: CategoryDao
) : TaskRepository {

    override fun getAllActiveTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllActiveTasks()
    }

    override suspend fun getTaskById(taskId: String): TaskEntity? {
        return taskDao.getTaskById(taskId)
    }

    override suspend fun insertTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    override suspend fun deleteTask(taskId: String) {
        taskDao.deleteTask(taskId)
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    override suspend fun insertCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }
}
