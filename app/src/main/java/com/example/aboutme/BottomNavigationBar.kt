package com.example.aboutme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavigation(
    navController: NavController,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = modifier) {
        // Bottom Navigation Bar
        BottomNavigation(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color.White
        ) {
            // Notes Item
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Note, contentDescription = "Notes") },
                label = { Text("Notlar") },
                selected = currentRoute == "notes",
                onClick = {
                    navController.navigate("notes") {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo("login_screen") {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )

            // Daily Notes Item
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.DateRange, contentDescription = "Daily Notes") },
                label = { Text("Günlük") },
                selected = currentRoute == "dailyNotes",
                onClick = {
                    navController.navigate("dailyNotes") {
                        popUpTo("login_screen") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // Empty space for FAB
            BottomNavigationItem(
                icon = { },
                label = { },
                selected = false,
                onClick = { },
                enabled = false
            )

            // Timer Item
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Timer, contentDescription = "Timer") },
                label = { Text("Zamanlayıcı") },
                selected = currentRoute == "timer",
                onClick = {
                    navController.navigate("timer") {
                        popUpTo("login_screen") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // Profile Item
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                label = { Text("Profil") },
                selected = currentRoute == "profile",
                onClick = {
                    navController.navigate("profile") {
                        popUpTo("login_screen") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // Floating Action Button (centered)
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-20).dp)
                .size(56.dp),
            shape = CircleShape,
            containerColor = Color(0xFFFB8C00)
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }
    }
}