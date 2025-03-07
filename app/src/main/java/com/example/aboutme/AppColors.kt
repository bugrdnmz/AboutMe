package com.example.aboutme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Dark Mode Colors
    val DarkCharcoal = Color(0xFF080A0D)
    val SandyBrown = Color(0xFFA68263)
    val DeepBrown = Color(0xFF593E2E)
    val TerraCotta = Color(0xFF8C654F)
    val LightGray = Color(0xFFD9D9D9)

    // Mavi-Turuncu Renk Paleti

        val NavyBlue = Color(0xFF104573)       // Koyu mavi - ana renk
        val Amber = Color(0xFFF2B035)          // Amber - vurgu rengi
        val LightAmber = Color(0xFFF2B872)     // Açık amber - ikonlar için
        val PeachBeige = Color(0xFFF2CDAC)     // Şeftali bej - detaylar için
        val LightBeige = Color(0xFFF2DBCE)     // Açık bej - arka plan// Koyu metin rengi
        val TextMedium = Color(0xFF2E5278)


    val Bronze = Color(0xFFA6763C)         // Bronz - ikincil vurgu
    val Sand = Color(0xFFD9CEC5)           // Kum rengi - arka plan
    val Terracotta = Color(0xFFD97941)     // Kiremit - ana vurgu rengi
    val DarkBrown = Color(0xFF73392C)      // Koyu kahverengi - ana renk
    val Rust = Color(0xFFA65341)           // Pas kırmızısı - detaylar için
    // Beyaz
    val LightSand123 = Color(0xFFF5F0EB)      // Açık kum rengi - kart arka planı
    val TextDark = Color(0xFF3E1F18)       // Koyu metin
    val TextLight = Color(0xFF73473C)      // Açık metin
    // Orta metin rengi

    // Adjusted color definitions
    val DirtyGray = Color(0xFF2C2C2C)
    val MudGray = Color(0xFF3E3E3E)

    val LightSand = Color(0xFFD2B48C)


    val DarkBlue = Color(0xFF1B778C)       // Koyu mavi - ana renk
    val MediumBlue = Color(0xFF66C4D9)     // Orta mavi - vurgu rengi
    val LightBlue = Color(0xFFA0EAF2)      // Açık mavi - detaylar için
    val OliveGreen = Color(0xFF537310)     // Zeytin yeşili - aksanlar için
    val SandBeige = Color(0xFFF2EBC9)      // Kum bej - arka plan
    val White = Color.White                // Beyaz

    val DarkGreen = Color(0xFF094036)      // Koyu yeşil - başlıklar için
    val DarkBackground = Color(0xFF060D0C)  // Çok koyu yeşil/siyah - alternatif arka plan
    val MediumGreen = Color(0xFF025940)     // Orta yeşil - vurgu rengi
    val LightGreen = Color(0xFF48D995)      // Açık yeşil - ikonlar ve detaylar için
    val BrightGreen = Color(0xFF50F296)     // Parlak yeşil - aksiyon butonları için// Beyaz
    val BackgroundGreen = Color(0xFFE6F8F1) // Çok açık yeşil - arka plan
    val TextDark12 = Color(0xFF0A2720)

    // Light Mode Colors
    val Sage = Color(0xFF8EA68A)       // Soft sage green
    val Cream = Color(0xFFF2EAC0)      // Warm cream
    val Apricot = Color(0xFFF2AA89)    // Soft apricot
    val Coral = Color(0xFFF27A65)      // Coral
    val Brick = Color(0xFFBF3939)

    val DarkGray = Color(0xFF7D858C)
    val Teal = Color(0xFF2B7B8C)
    val LightGray12 = Color(0xFFBFBBB8)
    val Background = Color(0xFFF2F2F2)
    val TextDark1 = Color(0xFF0D0D0D)

    val RustyRed = Color(0xFFA65341)
    val BurntOrange = Color(0xFFD97941)
    val WarmBeige = Color(0xFFA6763C)



    val LightBackground = Color(0xFFF0F2FF) // Çok açık mavimsi - alternatif arka plan
// Brick red
}



// Theme Configuration
data class AppColorScheme(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val error: Color
)

// Dark Mode Color Scheme
val DarkColorScheme = AppColorScheme(
    primary = AppColors.SandyBrown,
    secondary = AppColors.TerraCotta,
    background = AppColors.DarkCharcoal,
    surface = AppColors.DeepBrown,
    onPrimary = AppColors.LightGray,
    onSecondary = AppColors.LightGray,
    onBackground = AppColors.LightGray,
    onSurface = AppColors.LightGray,
    error = AppColors.Brick
)



// Light Mode Color Scheme
val LightColorScheme = AppColorScheme(
    primary = AppColors.Coral,
    secondary = AppColors.Apricot,
    background = AppColors.Cream,
    surface = AppColors.Cream.copy(alpha = 0.5f),
    onPrimary = AppColors.Cream,
    onSecondary = AppColors.Cream,
    onBackground = AppColors.Sage,
    onSurface = AppColors.Sage,
    error = AppColors.Brick
)