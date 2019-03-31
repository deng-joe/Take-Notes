package com.joey.takenotes.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Notes::class], version = 1)
abstract class NotesRoomDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao

    companion object {
        @Volatile
        var INSTANCE: NotesRoomDatabase? = null

        fun getInstance(context: Context): NotesRoomDatabase {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesRoomDatabase::class.java,
                    "notes_database"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
