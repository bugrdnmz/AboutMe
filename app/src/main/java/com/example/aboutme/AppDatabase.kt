package com.example.aboutme

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class, User::class, DailyNote::class, TimerRecord::class], version = 9)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun dailyNoteDao(): DailyNoteDao
    abstract fun timerDao(): TimerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_7_8, MIGRATION_8_9) // Yeni migration eklendi
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 7 to 8: Adding userId to all tables
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN userId INTEGER NOT NULL DEFAULT -1")
                database.execSQL("ALTER TABLE daily_notes ADD COLUMN userId INTEGER NOT NULL DEFAULT -1")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `timer_records` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `userId` INTEGER NOT NULL DEFAULT -1,
                        `description` TEXT NOT NULL,
                        `duration` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL
                    )
                    """
                )
            }
        }

        // Migration from version 8 to 9: Adding isCompleted column to daily_notes
        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE daily_notes ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}