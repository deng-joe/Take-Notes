package com.joey.takenotes.utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE take_notes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, body TEXT NOT NULL, date INTEGER NOT NULL)"
        )

        database.execSQL("INSERT INTO take_notes (id, title, body, date) SELECT id, title, body, date FROM notes_table")

        database.execSQL("DROP TABLE notes_database")

        database.execSQL("ALTER TABLE take_notes RENAME TO notes_database")
    }

}
