package com.example.aboutme

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyNoteDao {
    @Query("SELECT * FROM daily_notes WHERE day = :day AND userId = :userId ORDER BY hour ASC")
    suspend fun getNotesForDayAndUser(day: String, userId: String): List<DailyNote>

    // ID'ye göre notu getirme metodu
    @Query("SELECT * FROM daily_notes WHERE id = :id")
    suspend fun getNoteById(id: Long): DailyNote?

    // Not güncelleme metodu
    @Update
    suspend fun update(note: DailyNote)

    // Diğer mevcut metodlar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: DailyNote): Long

    @Delete
    suspend fun delete(note: DailyNote)

    // Bir kullanıcının tüm notlarını silmek için
    @Query("DELETE FROM daily_notes WHERE userId = :userId")
    suspend fun deleteAllUserNotes(userId: String)

    @Query("SELECT * FROM daily_notes WHERE day = :day")
    suspend fun getNotesForDay(day: String): List<DailyNote>

    // Kullanıcıya özel sorgu
    @Query("SELECT * FROM daily_notes WHERE userId = :userId AND day = :day")
    suspend fun getNotesForUserAndDay(userId: String, day: String): List<DailyNote>

    // Kullanıcıya özel tüm notları getir
    @Query("SELECT * FROM daily_notes WHERE userId = :userId ORDER BY day DESC")
    suspend fun getAllNotesForUser(userId: String): List<DailyNote>

    // Belirli bir kategorideki notları getir
    @Query("SELECT * FROM daily_notes WHERE userId = :userId AND category = :category")
    suspend fun getNotesForCategory(userId: String, category: String): List<DailyNote>

    // Tamamlanmış veya tamamlanmamış notları getir
    @Query("SELECT * FROM daily_notes WHERE userId = :userId AND isCompleted = :isCompleted")
    suspend fun getNotesForCompletionStatus(userId: String, isCompleted: Boolean): List<DailyNote>
}