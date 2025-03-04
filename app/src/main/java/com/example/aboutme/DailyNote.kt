package com.example.aboutme

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_notes")
data class DailyNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val day: String,      // Gün (örn: "1 Mart 2023")
    val hour: String,     // Saat (örn: "08:00")
    val note: String,     // Not başlığı

    // Başlık için stil özellikleri
    val titleFont: String = "Default",
    val titleColor: Int = 0,
    val titleWeight: String = "Normal",

    // İçerik için stil özellikleri
    val contentFont: String = "Default",
    val contentColor: Int = 0,
    val contentWeight: String = "Normal",

    // Veritabanınızda mevcut olan extra_column alanı
    val extra_column: String? = null  // Not içeriği için kullanılabilir
)