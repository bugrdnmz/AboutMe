package com.example.aboutme

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Veritabanı Entity sınıfı
@Entity(tableName = "habit_trackers")
data class HabitTracker(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val habitName: String,
    val startDate: Long = System.currentTimeMillis(),
    val lastCheckDate: Long? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val checkDates: List<Long> = emptyList(),
    val category: String = "Diğer"
)