package com.example.aboutme

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TimerDao {
    @Insert
    suspend fun insertTimer(timer: TimerRecord): Long

    @Delete
    suspend fun deleteTimer(timer: TimerRecord)

    @Query("SELECT * FROM timer_records ORDER BY date DESC")
    suspend fun getAllTimers(): List<TimerRecord>

    // Kullanıcıya özel sorgu ekleyelim
    @Query("SELECT * FROM timer_records WHERE userId = :userId ORDER BY date DESC")
    suspend fun getTimersForUser(userId: String?): List<TimerRecord>
}