package com.example.aboutme

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
@Dao
interface HabitTrackerDao {
    @Query("SELECT * FROM habit_trackers WHERE userId = :userId ORDER BY lastCheckDate DESC")
    suspend fun getHabitsForUser(userId: String): List<HabitTracker>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habitTracker: HabitTracker)

    @Transaction
    @Update
    suspend fun updateHabit(habitTracker: HabitTracker)

    @Transaction
    @Delete
    suspend fun deleteHabit(habitTracker: HabitTracker)
}