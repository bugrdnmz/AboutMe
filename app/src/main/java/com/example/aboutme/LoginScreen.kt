package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

// Toprak Tonları Paleti
object EarthColors {
    val Bronze = Color(0xFFA6763C)         // Bronz
    val Sand = Color(0xFFD9CEC5)           // Kum rengi
    val Terracotta = Color(0xFFD97941)     // Kiremit rengi
    val DarkBrown = Color(0xFF73392C)      // Koyu kahverengi
    val Rust = Color(0xFFA65341)           // Pas kırmızısı
    val LightSand = Color(0xFFF5F0EB)      // Açık kum rengi
    val TextDark = Color(0xFF3E1F18)       // Koyu metin
    val TextLight = Color(0xFF73473C)      // Açık metin
}

@Composable
fun LoginScreen(navController: NavController, userDao: UserDao, sessionManager: SessionManager) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        EarthColors.Sand,
                        EarthColors.LightSand
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Not Uygulaması",
                fontSize = 36.sp,
                color = EarthColors.DarkBrown,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Giriş",
                fontSize = 24.sp,
                color = EarthColors.Rust,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Login Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Kullanıcı Adı", color = EarthColors.TextLight) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EarthColors.Terracotta,
                            unfocusedBorderColor = EarthColors.Bronze.copy(alpha = 0.6f),
                            focusedTextColor = EarthColors.TextDark,
                            unfocusedTextColor = EarthColors.TextDark.copy(alpha = 0.8f),
                            cursorColor = EarthColors.Terracotta
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Şifre", color = EarthColors.TextLight) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EarthColors.Terracotta,
                            unfocusedBorderColor = EarthColors.Bronze.copy(alpha = 0.6f),
                            focusedTextColor = EarthColors.TextDark,
                            unfocusedTextColor = EarthColors.TextDark.copy(alpha = 0.8f),
                            cursorColor = EarthColors.Terracotta
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = EarthColors.Rust,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Login Button
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (username.isNotEmpty() && password.isNotEmpty()) {
                                coroutineScope.launch {
                                    val user = userDao.getUserByUsername(username)
                                    if (user != null && user.password == password) {
                                        // Save user session
                                        sessionManager.saveUserSession(user.id.toString(), user.username)

                                        // Navigate to main page
                                        navController.navigate("dailyNotes") {
                                            popUpTo("login_screen") { inclusive = true }
                                        }
                                    } else {
                                        errorMessage = "Geçersiz kullanıcı adı veya şifre"
                                    }
                                }
                            } else {
                                errorMessage = "Kullanıcı adı ve şifre gereklidir"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EarthColors.Terracotta),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Giriş Yap", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Register Button
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = EarthColors.DarkBrown
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(EarthColors.Bronze)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hesap Oluştur", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}