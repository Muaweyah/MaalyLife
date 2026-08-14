package com.maaly.life.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maaly.life.data.AppDatabase
import com.maaly.life.data.Category
import com.maaly.life.data.DefaultCategories
import com.maaly.life.data.Task
import com.maaly.life.data.TaskRepository
import com.maaly.life.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val categoryDao = AppDatabase.getInstance(application).categoryDao()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val appContext = application.applicationContext

    var currentDate: String = dateFormat.format(Date())
        private set

    val categories: List<Category> = DefaultCategories.list

    init {
        val dao = AppDatabase.getInstance(application).taskDao()
        repository = TaskRepository(dao)
        viewModelScope.launch {
            categoryDao.insertAll(DefaultCategories.list)
        }
    }

    fun tasksForCurrentDate(): Flow<List<Task>> = repository.getTasksForDate(currentDate)

    fun addTask(title: String, categoryId: String, reminderTime: String?) {
        viewModelScope.launch {
            val newId = repository.addTask(
                Task(title = title, category = categoryId, date = currentDate, reminderTime = reminderTime)
            )
            if (reminderTime != null) {
                ReminderScheduler.schedule(appContext, newId, title, currentDate, reminderTime)
            }
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            ReminderScheduler.cancel(appContext, task.id)
        }
    }
}
