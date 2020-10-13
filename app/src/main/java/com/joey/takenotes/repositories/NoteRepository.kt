package com.joey.takenotes.repositories

import androidx.lifecycle.LiveData
import com.joey.takenotes.data.Note
import com.joey.takenotes.data.NoteDao

/**
 * Declares the DAO as a private property in the constructor. Pass in the DAO instead of the whole
 * database because only access to the DAO is needed.
 */
class NoteRepository(private val noteDao: NoteDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) = noteDao.insert(note)

    suspend fun update(note: Note) = noteDao.update(note)

    suspend fun delete(note: Note) = noteDao.delete(note)

    suspend fun deleteAllNotes() = noteDao.deleteAllNotes()
}
