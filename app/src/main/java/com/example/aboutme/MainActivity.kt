package com.example.aboutme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aboutme.ui.theme.AboutMeTheme
import com.example.aboutme.ui.theme.SoftCream


class MainActivity : ComponentActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // SessionManager başlatma
        sessionManager = SessionManager(this)

        // Room Database başlatma
        val database = AppDatabase.getDatabase(this)
        val userDao = database.userDao()
        val taskDao = database.taskDao()
        val dailyNoteDao = database.dailyNoteDao()
        val timerDao = database.timerDao()

        setContent {
            AboutMeTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = SoftCream) { // Changed to SoftCream
                    CompositionLocalProvider(LocalSessionManager provides sessionManager) {
                        MainApp(
                            taskDao = taskDao,
                            userDao = userDao,
                            dailyNoteDao = dailyNoteDao,
                            timerDao = timerDao,
                            sessionManager = sessionManager
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainApp(
    taskDao: TaskDao,
    userDao: UserDao,
    dailyNoteDao: DailyNoteDao,
    timerDao: TimerDao,
    sessionManager: SessionManager
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Kullanıcı oturum durumuna göre başlangıç noktasını belirle
    val startDestination = if (sessionManager.isLoggedIn()) "notes" else "login_screen"

    // Her ekran için + butonunun işlevini kontrol etmek için state'ler
    var isAddingTask by remember { mutableStateOf(false) }
    var isAddingDailyNote by remember { mutableStateOf(false) }
    var isStartingTimer by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            if (currentRoute != "login_screen" && currentRoute != "register") {
                AppBottomNavigation(
                    navController = navController,
                    onAddClick = {
                        // Hangi sayfada olduğumuza göre farklı işlemler yap
                        when (currentRoute) {
                            "notes" -> isAddingTask = true
                            "dailyNotes" -> isAddingDailyNote = true
                            "timer" -> isStartingTimer = true
                            "profile" -> {
                                // Profil sayfasında + butonu özel bir işlem yapmayabilir
                                // veya profil düzenleme modunu açabilir
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login_screen") {
                LoginScreen(
                    navController = navController,
                    userDao = userDao,
                    sessionManager = sessionManager
                )
            }

            composable("register") {
                RegisterScreen(
                    navController = navController,
                    userDao = userDao,
                    sessionManager = sessionManager
                )
            }

            composable("notes") {
                TodoListScreen(
                    taskDao = taskDao,
                    navController = navController,
                    sessionManager = sessionManager,
                    isAddingTask = isAddingTask,
                    onAddingTaskComplete = { isAddingTask = false }
                )
            }

            composable("dailyNotes") {
                DailyNotesScreen(
                    dailyNoteDao = dailyNoteDao,
                    navController = navController,
                    sessionManager = sessionManager,
                    //isAddingDailyNote = isAddingDailyNote,
                   // onAddingNoteComplete = { isAddingDailyNote = false }
                )
            }

            composable("timer") {
                TimerScreen(
                    navController = navController,
                    sessionManager = sessionManager,
                    timerDao = timerDao,
                   // isStartingTimer = isStartingTimer,
                   // onTimerActionComplete = { isStartingTimer = false }
                )
            }

            composable("profile") {
                EnhancedProfileScreen(
                    navController = navController,
                    userDao = userDao,
                    sessionManager = sessionManager
                )
            }
        }
    }
}