package com.maaly.life.ui.stats

data class CategoryStat(
    val category: String,
    val total: Int,
    val completed: Int
) {
    val ratio: Float
        get() = if (total == 0) 0f else completed.toFloat() / total
}
