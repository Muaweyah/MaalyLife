package com.maaly.life.ui.calendar

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

enum class CalendarMode { DAY, WEEK, MONTH, YEAR }

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val dao: TaskDao = AppDatabase.getInstance(application).taskDao()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _mode = MutableStateFlow(CalendarMode.MONTH)
    val mode: StateFlow<CalendarMode> = _mode

    private val _referenceDate = MutableStateFlow(Calendar.getInstance())
    val referenceDate: StateFlow<Calendar> = _referenceDate

    private val _dayStatuses = MutableStateFlow<List<DayStatus>>(emptyList())
    val dayStatuses: StateFlow<List<DayStatus>> = _dayStatuses

    init {
        loadRange()
    }

    fun setMode(newMode: CalendarMode) {
        _mode.value = newMode
        loadRange()
    }

    fun goPrevious() {
        val cal = _referenceDate.value.clone() as Calendar
        when (_mode.value) {
            CalendarMode.DAY -> cal.add(Calendar.DAY_OF_YEAR, -1)
            CalendarMode.WEEK -> cal.add(Calendar.WEEK_OF_YEAR, -1)
            CalendarMode.MONTH -> cal.add(Calendar.MONTH, -1)
            CalendarMode.YEAR -> cal.add(Calendar.YEAR, -1)
        }
        _referenceDate.value = cal
        loadRange()
    }

    fun goNext() {
        val cal = _referenceDate.value.clone() as Calendar
        when (_mode.value) {
            CalendarMode.DAY -> cal.add(Calendar.DAY_OF_YEAR, 1)
            CalendarMode.WEEK -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            CalendarMode.MONTH -> cal.add(Calendar.MONTH, 1)
            CalendarMode.YEAR -> cal.add(Calendar.YEAR, 1)
        }
        _referenceDate.value = cal
        loadRange()
    }

    private fun loadRange() {
        viewModelScope.launch {
            val cal = _referenceDate.value.clone() as Calendar
            val start: Calendar
            val end: Calendar

            when (_mode.value) {
                CalendarMode.DAY -> {
                    start = cal.clone() as Calendar
                    end = cal.clone() as Calendar
                }
                CalendarMode.WEEK -> {
                    start = cal.clone() as Calendar
                    start.set(Calendar.DAY_OF_WEEK, start.firstDayOfWeek)
                    end = start.clone() as Calendar
                    end.add(Calendar.DAY_OF_YEAR, 6)
                }
                CalendarMode.MONTH -> {
                    start = cal.clone() as Calendar
                    start.set(Calendar.DAY_OF_MONTH, 1)
                    end = cal.clone() as Calendar
                    end.set(Calendar.DAY_OF_MONTH, end.getActualMaximum(Calendar.DAY_OF_MONTH))
                }
                CalendarMode.YEAR -> {
                    start = cal.clone() as Calendar
                    start.set(Calendar.DAY_OF_YEAR, 1)
                    end = cal.clone() as Calendar
                    end.set(Calendar.DAY_OF_YEAR, end.getActualMaximum(Calendar.DAY_OF_YEAR))
                }
            }

            val startStr = dateFormat.format(start.time)
            val endStr = dateFormat.format(end.time)
            val tasks = dao.getTasksInRange(startStr, endStr)

            val grouped = tasks.groupBy { it.date }
            val result = mutableListOf<DayStatus>()
            val iter = start.clone() as Calendar
            while (!iter.after(end)) {
                val d = dateFormat.format(iter.time)
                val dayTasks = grouped[d] ?: emptyList()
                result.add(
                    DayStatus(
                        date = d,
                        totalTasks = dayTasks.size,
                        completedTasks = dayTasks.count { it.isCompleted }
                    )
                )
                iter.add(Calendar.DAY_OF_YEAR, 1)
            }
            _dayStatuses.value = result
        }
    }
}
