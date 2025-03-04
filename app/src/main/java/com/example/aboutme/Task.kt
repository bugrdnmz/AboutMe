package com.example.aboutme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val taskName: String,
    val description: String,
    val color: Int,

    // Title styling properties
    val titleFontFamily: String = "",
    val titleTextColor: Int = 0,
    val titleFontSize: Int = 0,
    val titleFontWeight: String = "",

    // Description styling properties
    val descFontFamily: String = "",
    val descTextColor: Int = 0,
    val descFontSize: Int = 0,
    val descFontWeight: String = "",

    // For backward compatibility with older schema
    val fontFamily: String = "Default",
    val textColor: Int = Color.White.toArgb(),
    val fontSize: Int = 18,
    val fontWeight: String = "Normal"
)