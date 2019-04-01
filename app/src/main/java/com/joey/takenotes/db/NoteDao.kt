package com.joey.takenotes.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface NoteDao {
    @Insert(onConflict = REPLACE)
    fun insert(note: NoteEntity)

    @Update(onConflict = REPLACE)
    fun update(note: NoteEntity)

    @Delete
    fun delete(note: NoteEntity)

    @Query("SELECT * FROM take_notes")
    fun getAllNotes(): LiveData<List<NoteEntity>>

    @Query("DELETE FROM take_notes")
    fun deleteAllNotes()
}
