package com.joey.takenotes.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface NoteDao {
    @Insert(onConflict = REPLACE)
    fun insert(note: Note)

    @Update(onConflict = REPLACE)
    fun update(note: Note)

    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM take_notes")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("DELETE FROM take_notes")
    fun deleteAllNotes()
}
