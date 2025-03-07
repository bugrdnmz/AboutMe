package com.example.aboutme

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Task::class, User::class, DailyNote::class, TimerRecord::class, HabitTracker::class],
    version = 10
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun dailyNoteDao(): DailyNoteDao
    abstract fun timerDao(): TimerDao
    abstract fun habitTrackerDao(): HabitTrackerDao

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
                    .addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 7 to 8: Adding userId to all tables
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE tasks ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE daily_notes ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `timer_records` (
                        `id` TEXT PRIMARY KEY NOT NULL,
                        `userId` TEXT NOT NULL,
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

        // Migration from version 9 to 10: Adding habit_trackers table
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `habit_trackers` (
                        `id` TEXT NOT NULL PRIMARY KEY,
                        `userId` TEXT NOT NULL,
                        `habitName` TEXT NOT NULL,
                        `startDate` INTEGER NOT NULL,
                        `lastCheckDate` INTEGER,
                        `currentStreak` INTEGER NOT NULL,
                        `bestStreak` INTEGER NOT NULL,
                        `checkDates` TEXT NOT NULL,
                        `category` TEXT NOT NULL
                    )
                """)
            }
        }
    }
}