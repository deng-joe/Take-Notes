package com.joey.takenotes.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.joey.takenotes.db.Notes
import com.joey.takenotes.db.NotesDao

class NotesRepository(private val notesDao: NotesDao) {
    val allNotes: LiveData<List<Notes>> = notesDao.getAllNotes()

    @WorkerThread
    fun insert(notes: Notes) {
        notesDao.insert(notes)
    }

    @WorkerThread
    fun update(notes: Notes) {
        notesDao.update(notes)
    }

    @WorkerThread
    fun delete(notes: Notes) {
        notesDao.delete(notes)
    }

    @WorkerThread
    fun deleteAllNotes() {
        notesDao.getAllNotes()
    }
}
