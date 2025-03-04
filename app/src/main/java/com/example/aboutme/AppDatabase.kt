package com.example.aboutme

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Task::class, User::class, DailyNote::class], version = 7) // Increased version number
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun dailyNoteDao(): DailyNoteDao

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
                    .addMigrations(
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7 // Added new migration
                    )
                    // Alternative approach if you're in development and don't need to preserve data:
                    // .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 3 to 4: Creating DailyNote table
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_notes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `day` TEXT NOT NULL,
                        `hour` TEXT NOT NULL,
                        `note` TEXT
                    )
                    """
                )
            }
        }

        // Migration from version 4 to 5: Adding extra_column to daily_notes table
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `daily_notes` ADD COLUMN `extra_column` TEXT DEFAULT NULL")
            }
        }

        // Migration from version 5 to 6: Handling the schema change
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new tasks table
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `tasks_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `taskName` TEXT NOT NULL,
                        `description` TEXT NOT NULL,
                        `color` INTEGER NOT NULL DEFAULT -7829368,
                        `fontFamily` TEXT NOT NULL DEFAULT 'Default',
                        `fontSize` INTEGER NOT NULL DEFAULT 18,
                        `fontWeight` TEXT NOT NULL DEFAULT 'Normal',
                        `textColor` INTEGER NOT NULL DEFAULT -1
                    )
                    """
                )

                // Move old data to new table
                database.execSQL(
                    """
                    INSERT INTO tasks_new (id, taskName, description, color, fontFamily, fontSize, fontWeight, textColor)
                    SELECT id, taskName, description, color, fontFamily, fontSize, fontWeight, textColor FROM tasks
                    """
                )

                // Drop old table
                database.execSQL("DROP TABLE tasks")

                // Rename new table to original name
                database.execSQL("ALTER TABLE tasks_new RENAME TO tasks")
            }
        }

        // Migration from version 6 to 7: Adding separate styling columns for title and description
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns for title styling
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `titleFontFamily` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `titleTextColor` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `titleFontSize` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `titleFontWeight` TEXT NOT NULL DEFAULT ''")

                // Add new columns for description styling
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `descFontFamily` TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `descTextColor` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `descFontSize` INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE `tasks` ADD COLUMN `descFontWeight` TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}