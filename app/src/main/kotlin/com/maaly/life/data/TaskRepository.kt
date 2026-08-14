package com.maaly.life.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getTasksForDate(date: String): Flow<List<Task>> = taskDao.getTasksForDate(date)

    suspend fun addTask(task: Task) = taskDao.insert(task)

    suspend fun updateTask(task: Task) = taskDao.update(task)

    suspend fun deleteTask(task: Task) = taskDao.delete(task)

    suspend fun completionRatio(date: String): Pair<Int, Int> {
        val completed = taskDao.getCompletedCount(date)
        val total = taskDao.getTotalCount(date)
        return completed to total
    }
}
