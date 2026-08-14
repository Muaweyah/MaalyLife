package com.maaly.life.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLog(
    @PrimaryKey val date: String,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0
)
