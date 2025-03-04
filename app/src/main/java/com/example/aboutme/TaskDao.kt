package com.example.aboutme

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    // Diğer fonksiyonlar

    @Update
    suspend fun updateTask(task: Task)

    @Insert
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>
}
