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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


val WarmBeige = Color(0xFFA6763C)     // #A6763C
val SoftCream = Color(0xFFD9CEC5)     // #D9CEC5
val BurntOrange = Color(0xFFD97941)   // #D97941
val DeepBrown = Color(0xFF73392C)     // #73392C
val RustyRed = Color(0xFFA65341)      // #A65341

@Composable
fun AppBottomNavigation(
    navController: NavController,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Skip rendering if on login or register screens
    if (currentRoute == "login_screen" || currentRoute == "register") {
        return
    }

    Box(modifier = modifier) {
        // Bottom Navigation Bar
        BottomNavigation(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = DeepBrown,
            contentColor = SoftCream
        ) {
            // Notlar Item - Sadece ikon
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Filled.Note,
                        contentDescription = "Notlar",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = null, // Metni kaldırdık
                selected = currentRoute == "notes",
                selectedContentColor = BurntOrange,
                unselectedContentColor = SoftCream.copy(alpha = 0.7f),
                onClick = {
                    navController.navigate("notes") {
                        popUpTo("login_screen") {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            // Günlük Item - Sadece ikon
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = "Günlük",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = null, // Metni kaldırdık
                selected = currentRoute == "dailyNotes",
                selectedContentColor = BurntOrange,
                unselectedContentColor = SoftCream.copy(alpha = 0.7f),
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
                label = null, // Metni kaldırdık
                selected = false,
                onClick = { },
                enabled = false
            )

            // Timer Item - Sadece ikon
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Filled.Timer,
                        contentDescription = "Zamanlayıcı",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = null, // Metni kaldırdık
                selected = currentRoute == "timer",
                selectedContentColor = BurntOrange,
                unselectedContentColor = SoftCream.copy(alpha = 0.7f),
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

            // Profil Item - Sadece ikon
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "Profil",
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = null, // Metni kaldırdık
                selected = currentRoute == "profile",
                selectedContentColor = BurntOrange,
                unselectedContentColor = SoftCream.copy(alpha = 0.7f),
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
            containerColor = RustyRed
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = "Add",
                tint = SoftCream
            )
        }
    }
}