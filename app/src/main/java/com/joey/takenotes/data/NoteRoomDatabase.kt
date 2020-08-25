package com.joey.takenotes.data

import com.joey.takenotes.utils.MIGRATION_1_2
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.joey.takenotes.utils.DateConverter

/**
 * Annotates class to be a Room Database with a table (entity) of the Note class.
 */
@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class NoteRoomDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        // Singleton (NoteRoomDatabase) prevents multiple instances of database opening at the same time
        @Volatile
        var INSTANCE: NoteRoomDatabase? = null
        private var DATABASE_NAME = "notes_database"

        fun getInstance(context: Context): NoteRoomDatabase {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteRoomDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
