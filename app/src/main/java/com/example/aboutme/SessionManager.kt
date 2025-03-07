package com.example.aboutme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf

class SessionManager(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        return !getUserId().isNullOrEmpty()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun saveUserSession(userId: String, username: String) {
        sharedPreferences.edit().apply {
            putString("user_id", userId)
            putString("username", username)
            apply()
        }
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}

// Composition Local for session manager access across composables
val LocalSessionManager = compositionLocalOf<SessionManager> { error("SessionManager not provided") }