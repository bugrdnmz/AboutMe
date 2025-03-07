package com.example.aboutme

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_records")
data class TimerRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val userId: String,
    val description: String,
    val category: String = "Diğer", // Default category
    val duration: Long,
    val date: Long = System.currentTimeMillis() // Automatically set to current time
)
