package com.joey.takenotes.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.joey.takenotes.db.NoteEntity
import com.joey.takenotes.db.NoteDao

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<NoteEntity>> = noteDao.getAllNotes()

    @WorkerThread
    fun insert(note: NoteEntity) {
        noteDao.insert(note)
    }

    @WorkerThread
    fun update(note: NoteEntity) {
        noteDao.update(note)
    }

    @WorkerThread
    fun delete(note: NoteEntity) {
        noteDao.delete(note)
    }

    @WorkerThread
    fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
}
