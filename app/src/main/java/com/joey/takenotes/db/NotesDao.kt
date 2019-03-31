package com.joey.takenotes.db

import androidx.room.*

@Dao
interface NotesDao {
    @Insert
    fun insert(notes: Notes)

    @Update
    fun update(notes: Notes)

    @Delete
    fun delete(notes: Notes)

    @Query("SELECT * FROM notes ORDER BY id")
    fun getAllNotes(): List<Notes>

    @Query("DELETE FROM notes")
    fun deleteAllNotes()
}
