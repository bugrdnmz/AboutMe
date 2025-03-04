package com.example.aboutme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aboutme.ui.theme.AboutMeTheme

// Eğer ayrı TimerScreen dosyanız varsa bu import eklenecek
// import com.example.aboutme.timer.TimerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Room Database initialization
        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val taskDao = database.taskDao()
        val dailyNoteDao = database.dailyNoteDao()

        setContent {
            AboutMeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainApp(taskDao = taskDao, userDao = userDao, dailyNoteDao = dailyNoteDao)
                }
            }
        }
    }
}

@Composable
fun MainApp(taskDao: TaskDao, userDao: UserDao, dailyNoteDao: DailyNoteDao) {
    val navController = rememberNavController()

    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login_screen",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login_screen") {
                LoginScreen(navController = navController, userDao = userDao)
            }

            composable("register") {
                RegisterScreen(navController = navController, userDao = userDao)
            }

            // After login, we navigate to notes screen
            composable("notes") {
                TodoListScreen(taskDao = taskDao, navController = navController)
            }

            composable("dailyNotes") {
                DailyNotesScreen(dailyNoteDao = dailyNoteDao,navController = navController)
            }

            composable("timer") {
                // Eğer ayrı bir TimerScreen dosyanız varsa, aşağıdaki satır kullanılır
                 TimerScreen(navController = navController)

                // Eğer ayrı bir TimerScreen dosyanız yoksa, placeholder kullanın
                TimerScreenPlaceholder(navController = navController)
            }

            composable("profile") {
                // Eğer ayrı bir ProfileScreen dosyanız varsa, aşağıdaki satır kullanılır
                // ProfileScreen(navController = navController, userDao = userDao)

                // Eğer ayrı bir ProfileScreen dosyanız yoksa, placeholder kullanın
                ProfileScreenPlaceholder(navController = navController, userDao = userDao)
            }
        }
    }
}

// Placeholder versiyon - sadece gerçek implementasyonunuz yoksa kullanın
@Composable
fun TimerScreenPlaceholder(navController: NavController) {
    PlaceholderScreen("Timer Screen", navController)
}

// Placeholder versiyon - sadece gerçek implementasyonunuz yoksa kullanın
@Composable
fun ProfileScreenPlaceholder(navController: NavController, userDao: UserDao) {
    PlaceholderScreen("Profile Screen", navController)
}

@Composable
fun PlaceholderScreen(screenName: String, navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = screenName, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "This screen is coming soon!")
        }

        // Bottom Navigation
        AppBottomNavigation(
            navController = navController,
            onAddClick = { /* Handle add action */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}