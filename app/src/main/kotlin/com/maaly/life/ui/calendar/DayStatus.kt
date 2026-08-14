package com.maaly.life.ui.calendar

data class DayStatus(
    val date: String,
    val totalTasks: Int,
    val completedTasks: Int
) {
    val ratio: Float
        get() = if (totalTasks == 0) 0f else completedTasks.toFloat() / totalTasks
}
