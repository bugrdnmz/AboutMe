package com.example.aboutme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    // Ayarlar ekranı içeriği
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ayarlar",
            fontSize = 30.sp,
            color = Color(0xFFFB8C00),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsCategory(title = "Genel")

        SettingsItem(
            icon = Icons.Default.Palette,
            title = "Tema",
            subtitle = "Açık"
        )

        SettingsItem(
            icon = Icons.Default.Language,
            title = "Dil",
            subtitle = "Türkçe"
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        SettingsCategory(title = "Bildirimler")

        var pushNotifications by remember { mutableStateOf(true) }
       /* SettingsToggleItem(
            icon = Icons.Default.Notifications,
            title = "Anlık Bildirimler",
            isChecked = pushNotifications,
            onCheckedChange = { pushNotifications = it }
        )

        var emailNotifications by remember { mutableStateOf(false) }
        SettingsToggleItem(
            icon = Icons.Default.Email,
            title = "E-posta Bildirimleri",
            isChecked = emailNotifications,
            onCheckedChange = { emailNotifications = it }
        )
*/
        Divider(modifier = Modifier.padding(vertical = 16.dp))

        SettingsCategory(title = "Uygulama Hakkında")

        SettingsItem(
            icon = Icons.Default.Info,
            title = "Sürüm",
            subtitle = "1.0.0"
        )

        SettingsItem(
            icon = Icons.Default.Description,
            title = "Lisans",
            subtitle = "MIT"
        )
    }
}

@Composable
fun SettingsCategory(title: String) {
    Text(
        text = title,
        color = Color(0xFFFB8C00),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFFFB8C00),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = Color.Gray
        )
    }
}