package com.example.aboutme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, userDao: UserDao) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo or App Name
            Text(
                text = "Hoş Geldiniz",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFB8C00),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Main Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Giriş Yap",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFB8C00),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Kullanıcı Adı Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Kullanıcı Adı") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Kullanıcı Adı"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    // Şifre Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Şifre") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Şifre"
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Şifreyi Gizle" else "Şifreyi Göster"
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    // Hata Mesajı
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Giriş Butonu
                    Button(
                        onClick = {
                            // Giriş bilgilerini doğrula
                            if (username.isBlank() || password.isBlank()) {
                                errorMessage = "Lütfen kullanıcı adı ve şifre alanlarını doldurun"
                                return@Button
                            }

                            isLoading = true
                            errorMessage = ""

                            coroutineScope.launch {
                                try {
                                    // Kullanıcı adı ve şifreyi doğrula
                                    val userId = userDao.validateUser(username, password)

                                    if (userId != null) {
                                        // Giriş başarılı - "todo_list_screen" yerine "notes" kullanılacak
                                        navController.navigate("notes") {
                                            popUpTo("login_screen") { inclusive = true }
                                        }
                                    } else {
                                        // Kullanıcı bulunamadı veya şifre yanlış
                                        errorMessage = "Kullanıcı adı veya şifre hatalı"
                                        Toast.makeText(context, "Giriş başarısız", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    // Hata durumunda bilgilendir
                                    errorMessage = "Giriş sırasında bir hata oluştu: ${e.localizedMessage}"
                                    Toast.makeText(context, "Bir hata oluştu", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFB8C00)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Giriş Yap")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Kayıt Linki
                    TextButton(
                        onClick = {
                            navController.navigate("register")
                        }
                    ) {
                        Text(
                            text = "Hesabın yok mu? Kaydol",
                            color = Color(0xFFFB8C00)
                        )
                    }
                }
            }
        }
    }
}