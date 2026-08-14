package com.maaly.life.ui.stats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maaly.life.data.AppDatabase
import com.maaly.life.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class StatsPeriod { WEEK, MONTH }

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: TaskDao = AppDatabase.getInstance(application).taskDao()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _period = MutableStateFlow(StatsPeriod.WEEK)
    val period: StateFlow<StatsPeriod> = _period

    private val _categoryStats = MutableStateFlow<List<CategoryStat>>(emptyList())
    val categoryStats: StateFlow<List<CategoryStat>> = _categoryStats

    private val _overallRatio = MutableStateFlow(0f)
    val overallRatio: StateFlow<Float> = _overallRatio

    init {
        load()
    }

    fun setPeriod(newPeriod: StatsPeriod) {
        _period.value = newPeriod
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            val end = cal.clone() as Calendar
            val start = cal.clone() as Calendar

            if (_period.value == StatsPeriod.WEEK) {
                start.set(Calendar.DAY_OF_WEEK, start.firstDayOfWeek)
            } else {
                start.set(Calendar.DAY_OF_MONTH, 1)
                end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH))
            }

            val startStr = dateFormat.format(start.time)
            val endStr = dateFormat.format(end.time)
            val tasks = dao.getTasksInRange(startStr, endStr)

            val grouped = tasks.groupBy { it.category }
            _categoryStats.value = grouped.map { (category, list) ->
                CategoryStat(
                    category = category,
                    total = list.size,
                    completed = list.count { it.isCompleted }
                )
            }.sortedByDescending { it.total }

            _overallRatio.value = if (tasks.isEmpty()) 0f
                else tasks.count { it.isCompleted }.toFloat() / tasks.size
        }
    }
}
