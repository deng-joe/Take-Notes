package com.joey.takenotes.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.joey.takenotes.db.Note
import com.joey.takenotes.db.NoteDao

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    @WorkerThread
    fun insert(note: Note) {
        noteDao.insert(note)
    }

    @WorkerThread
    fun update(note: Note) {
        noteDao.update(note)
    }

    @WorkerThread
    fun delete(note: Note) {
        noteDao.delete(note)
    }

    @WorkerThread
    fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
}
