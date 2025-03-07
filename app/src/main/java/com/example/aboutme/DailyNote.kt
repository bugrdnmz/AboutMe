package com.example.aboutme

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_notes")
// Original DailyNote data class (assumed to be defined elsewhere)
data class DailyNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val day: String,
    val hour: String,
    val note: String,
    val titleFont: String,
    val titleColor: Int,
    val titleWeight: String,
    val contentFont: String,
    val contentColor: Int,
    val contentWeight: String,
    val extra_column: String? = null,
    // Yeni alanlar
    val isCompleted: Boolean = false,
    val category: String = TaskCategory.OTHER.name, // Enum adını string olarak saklıyoruz
    val hasNotification: Boolean = false,
    val notificationTime: String? = null,
    val hasLocationAlert: Boolean = false,
    val locationName: String? = null
)