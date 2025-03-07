package com.example.aboutme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

// Geliştirilmiş günlük not sınıfı
data class EnhancedDailyNote(
    val id: Long = 0,
    val userId: String,
    val day: String,
    val hour: String,
    val note: String,
    val titleFont: String? = null,
    val titleColor: Int = Color.White.toArgb(),
    val titleWeight: String? = null,
    val contentFont: String? = null,
    val contentColor: Int = Color.White.toArgb(),
    val contentWeight: String? = null,
    val extra_column: String? = null,
    // Yeni özellikler
    val isCompleted: Boolean = false,
    val category: TaskCategory = TaskCategory.OTHER,
    val hasNotification: Boolean = false,
    val notificationTime: String? = null,
    val hasLocationAlert: Boolean = false,
    val locationName: String? = null
)
