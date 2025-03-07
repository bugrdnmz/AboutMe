package com.example.aboutme

import androidx.compose.ui.graphics.Color

// Görev kategorileri için özel renk düzenlemeleri
enum class TaskCategory(val label: String, val color: Color) {
    WORK("İş", BurntOrange),
    PERSONAL("Kişisel", RustyRed),
    MEETING("Toplantı", WarmBeige),
    DEADLINE("Okul", DeepBrown),
    OTHER("Diğer", Color(0xFF8C9A9E))
}