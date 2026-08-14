package com.maaly.life.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id: String,
    val nameAr: String,
    val nameEn: String,
    val icon: String,
    val colorHex: String,
    val isHidden: Boolean = false,
    val isCustom: Boolean = false
)
