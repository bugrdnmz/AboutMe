package com.example.aboutme

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DailyNoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailyNote: DailyNote): Long

    @Query("SELECT * FROM daily_notes WHERE day = :day ORDER BY hour ASC")
    suspend fun getNotesForDay(day: String): List<DailyNote>

    @Delete
    suspend fun delete(dailyNote: DailyNote)

    @Query("DELETE FROM daily_notes WHERE day = :day")
    suspend fun deleteNotesForDay(day: String)

    @Query("DELETE FROM daily_notes")
    suspend fun deleteAllNotes()
}