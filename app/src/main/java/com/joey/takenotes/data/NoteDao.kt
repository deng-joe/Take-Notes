package com.joey.takenotes.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

/**
 * SQL queries on the notes.
 * LiveData is used to observe data for any changes.
 */
@Dao
interface NoteDao {
    @Insert(onConflict = REPLACE)
    fun insert(note: Note)

    @Update(onConflict = REPLACE)
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM take_notes ORDER BY date DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("DELETE FROM take_notes")
    fun deleteAllNotes()
}
