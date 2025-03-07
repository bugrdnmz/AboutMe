package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun EnhancedProfileScreen(
    navController: NavController,
    userDao: UserDao,
    sessionManager: SessionManager,
    habitTrackerDao: HabitTrackerDao? = null
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Kullanıcı bilgileri
    val username = sessionManager.getUsername() ?: "Kullanıcı Adı"
    val email = "kullanici@ornek.com"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Sand)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profil",
                fontSize = 30.sp,
                color = AppColors.DarkBrown,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profil resmi
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(AppColors.DarkBrown)
                    .border(3.dp, AppColors.Terracotta, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = username.firstOrNull()?.toString()?.uppercase() ?: "?",
                    color = AppColors.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kullanıcı bilgileri
            Text(
                text = username,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextDark
            )

            Text(
                text = email,
                fontSize = 16.sp,
                color = AppColors.TextLight
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Basitleştirilmiş alışkanlık takibi bileşeni
            SimpleHabitTracker(
                sessionManager = sessionManager,
                habitTrackerDao = habitTrackerDao
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Çıkış yap butonu
            Button(
                onClick = {
                    sessionManager.logout()
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Rust,
                    contentColor = AppColors.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = AppColors.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Çıkış Yap",
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}