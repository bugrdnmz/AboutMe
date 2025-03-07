package com.example.aboutme

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String, // Kullanıcı ID'si eklendi
    val taskName: String,
    val description: String,
    val color: Int,
    val fontFamily: String,
    val fontSize: Int,
    val fontWeight: String,
    val textColor: Int,
    val titleFontFamily: String = "",
    val titleTextColor: Int = 0,
    val titleFontSize: Int = 0,
    val titleFontWeight: String = "",
    val descFontFamily: String = "",
    val descTextColor: Int = 0,
    val descFontSize: Int = 0,
    val descFontWeight: String = ""
)