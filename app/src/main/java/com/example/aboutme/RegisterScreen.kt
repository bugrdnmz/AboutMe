package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aboutme.AppColors.BurntOrange
import com.example.aboutme.AppColors.RustyRed
import com.example.aboutme.AppColors.WarmBeige
import com.example.aboutme.ui.theme.SoftCream
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(navController: NavController, userDao: UserDao, sessionManager: SessionManager) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepBrown,        // Changed to DeepBrown
                        DeepBrown.copy(alpha = 0.8f)  // Subtle gradient
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hesap Oluştur",
                fontSize = 32.sp,
                color = BurntOrange,    // Changed to BurntOrange
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Uygulamayı kullanmak için kayıt olun",
                fontSize = 16.sp,
                color = SoftCream.copy(alpha = 0.8f),    // Changed to SoftCream
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Register Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = DeepBrown.copy(alpha = 0.5f)    // Changed to semi-transparent DeepBrown
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Kullanıcı Adı", color = SoftCream) },    // Changed to SoftCream
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BurntOrange,    // Changed to BurntOrange
                            unfocusedBorderColor = SoftCream.copy(alpha = 0.6f),    // Changed to SoftCream
                            focusedTextColor = SoftCream,    // Changed to SoftCream
                            unfocusedTextColor = SoftCream.copy(alpha = 0.8f),    // Changed to SoftCream
                            cursorColor = BurntOrange    // Changed to BurntOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("E-posta (Opsiyonel)", color = SoftCream) },    // Changed to SoftCream
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BurntOrange,    // Changed to BurntOrange
                            unfocusedBorderColor = SoftCream.copy(alpha = 0.6f),    // Changed to SoftCream
                            focusedTextColor = SoftCream,    // Changed to SoftCream
                            unfocusedTextColor = SoftCream.copy(alpha = 0.8f),    // Changed to SoftCream
                            cursorColor = BurntOrange    // Changed to BurntOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Şifre", color = SoftCream) },    // Changed to SoftCream
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BurntOrange,    // Changed to BurntOrange
                            unfocusedBorderColor = SoftCream.copy(alpha = 0.6f),    // Changed to SoftCream
                            focusedTextColor = SoftCream,    // Changed to SoftCream
                            unfocusedTextColor = SoftCream.copy(alpha = 0.8f),    // Changed to SoftCream
                            cursorColor = BurntOrange    // Changed to BurntOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Confirm Password Field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Şifre Tekrar", color = SoftCream) },    // Changed to SoftCream
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BurntOrange,    // Changed to BurntOrange
                            unfocusedBorderColor = SoftCream.copy(alpha = 0.6f),    // Changed to SoftCream
                            focusedTextColor = SoftCream,    // Changed to SoftCream
                            unfocusedTextColor = SoftCream.copy(alpha = 0.8f),    // Changed to SoftCream
                            cursorColor = BurntOrange    // Changed to BurntOrange
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Error Message
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = RustyRed,    // Changed to RustyRed for error messages
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    coroutineScope.launch {
                        if (username.isBlank() || password.isBlank()) {
                            errorMessage = "Kullanıcı adı ve şifre boş olamaz"
                            return@launch
                        }

                        if (password != confirmPassword) {
                            errorMessage = "Şifreler eşleşmiyor"
                            return@launch
                        }

                        val existingUser = userDao.getUserByUsername(username)
                        if (existingUser != null) {
                            errorMessage = "Bu kullanıcı adı zaten kullanımda"
                            return@launch
                        }

                        val user = User(
                            username = username,
                            password = password,
                            email = email
                        )
                        val userId = userDao.insertUser(user)
                        sessionManager.saveUserSession(userId.toInt().toString(), username)
                        navController.navigate("dailyNotes") {
                            popUpTo("login_screen") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BurntOrange),    // Changed to BurntOrange
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Kayıt Ol",
                    fontSize = 16.sp,
                    color = SoftCream    // Changed to SoftCream
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back Button
            OutlinedButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SoftCream    // Changed to SoftCream
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(WarmBeige)    // Changed to WarmBeige
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Geri Dön",
                    fontSize = 16.sp
                )
            }
        }
    }
}