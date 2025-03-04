package com.example.aboutme

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavController, userDao: UserDao) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Kayıt Ol",
            fontSize = 30.sp,
            color = Color(0xFFFB8C00),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Kullanıcı Adı") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Şifreyi Onayla") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Display error message if any
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Validate input
                when {
                    username.isBlank() -> {
                        errorMessage = "Kullanıcı adı boş bırakılamaz"
                        return@Button
                    }
                    email.isBlank() -> {
                        errorMessage = "E-posta boş bırakılamaz"
                        return@Button
                    }
                    password.isBlank() -> {
                        errorMessage = "Şifre boş bırakılamaz"
                        return@Button
                    }
                    password != confirmPassword -> {
                        errorMessage = "Şifreler eşleşmiyor"
                        return@Button
                    }
                }

                // Perform registration
                coroutineScope.launch {
                    try {
                        // Check if username already exists
                        val usernameCount = userDao.usernameExists(username)
                        if (usernameCount > 0) {
                            errorMessage = "Bu kullanıcı adı zaten kullanılıyor"
                            return@launch
                        }

                        // Create new user
                        val newUser = User(
                            username = username,
                            email = email,
                            password = password
                        )
                        val userId = userDao.insertUser(newUser)

                        if (userId != -1L) {
                            // Kayıt başarılı
                            Toast.makeText(context, "Kayıt başarılı", Toast.LENGTH_SHORT).show()

                            // "todo_list_screen" yerine "notes" rotasına git
                            navController.navigate("notes") {
                                popUpTo("register") { inclusive = true }
                            }
                        } else {
                            // Kayıt sırasında bir sorun oluştu
                            errorMessage = "Kayıt sırasında bir hata oluştu"
                        }
                    } catch (e: Exception) {
                        // Hata durumunda bilgilendir
                        errorMessage = "Kayıt sırasında bir hata oluştu: ${e.localizedMessage}"
                        Toast.makeText(context, "Bir hata oluştu", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFB8C00)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kayıt Ol")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Zaten hesabınız var mı?")
            TextButton(onClick = { navController.navigate("login_screen") }) {
                Text("Giriş Yap", color = Color(0xFFFB8C00))
            }
        }
    }
}