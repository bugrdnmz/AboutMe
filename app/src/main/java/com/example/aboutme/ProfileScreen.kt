package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Toprak Tonları Paleti


@Composable
fun EnhancedProfileScreen(
    navController: NavController,
    userDao: UserDao,
    sessionManager: SessionManager
) {
    val username = sessionManager.getUsername() ?: "Kullanıcı Adı"
    val email = "kullanici@ornek.com" // Gerçek e-postayı almak için veri tabanından sorgulama yapılabilir

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Sand)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 80.dp), // Bottom Navigation için alan bırak
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

            // Alışkanlık Takibi Bölümü
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.LightSand),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppColors.Bronze.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Habits",
                                tint = AppColors.Terracotta,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Alışkanlık Takibi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.DarkBrown
                        )
                    }

                    // Alışkanlık Kategorileri
                    HabitCategoryItem(
                        icon = Icons.Default.FitnessCenter,
                        title = "Spor & Fitness",
                        description = "Egzersiz rutinlerinizi takip edin",
                        onClick = {
                            // navController.navigate("habit_tracking/fitness")
                        }
                    )

                    Divider(
                        color = AppColors.Sand,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    HabitCategoryItem(
                        icon = Icons.Default.MenuBook,
                        title = "Okuma & Öğrenme",
                        description = "Okuma ve öğrenme alışkanlıklarınızı geliştirin",
                        onClick = {
                            // navController.navigate("habit_tracking/learning")
                        }
                    )

                    Divider(
                        color = AppColors.Sand,
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    HabitCategoryItem(
                        icon = Icons.Default.WaterDrop,
                        title = "Sağlık & İyilik",
                        description = "Su içme, uyku düzeni gibi sağlık alışkanlıklarınızı takip edin",
                        onClick = {
                            // navController.navigate("habit_tracking/health")
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // navController.navigate("create_habit")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Terracotta,
                            contentColor = AppColors.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Add Habit",
                            tint = AppColors.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Yeni Alışkanlık Ekle",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Profil ayarları
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.LightSand),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AppColors.Bronze.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Settings",
                                tint = AppColors.Terracotta,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Ayarlar",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.DarkBrown
                        )
                    }

                    ProfileSettingItem(
                        icon = Icons.Default.Edit,
                        title = "Profili Düzenle",
                        tintColor = AppColors.Bronze
                    )

                    ProfileSettingItem(
                        icon = Icons.Default.Notifications,
                        title = "Bildirim Ayarları",
                        tintColor = AppColors.Bronze
                    )

                    ProfileSettingItem(
                        icon = Icons.Default.Lock,
                        title = "Gizlilik ve Güvenlik",
                        tintColor = AppColors.Bronze
                    )

                    ProfileSettingItem(
                        icon = Icons.Default.Help,
                        title = "Yardım ve Destek",
                        tintColor = AppColors.Bronze
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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

@Composable
fun HabitCategoryItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AppColors.Bronze.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AppColors.DarkBrown,
                modifier = Modifier.size(26.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.TextDark
            )

            Text(
                text = description,
                fontSize = 14.sp,
                color = AppColors.TextLight
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Go to feature",
            tint = AppColors.Terracotta,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ProfileSettingItem(
    icon: ImageVector,
    title: String,
    tintColor: Color = AppColors.Bronze
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = AppColors.TextDark
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Arrow",
            tint = AppColors.Terracotta
        )
    }
}