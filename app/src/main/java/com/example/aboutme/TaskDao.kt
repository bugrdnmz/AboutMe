package com.example.aboutme

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Insert
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks ORDER BY id DESC")
    suspend fun getAllTasks(): List<Task>

    // Kullanıcıya özel sorgu ekleyelim
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    suspend fun getTasksForUser(userId: String?): List<Task>
}