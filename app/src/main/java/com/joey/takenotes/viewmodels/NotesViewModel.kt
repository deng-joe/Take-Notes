package com.joey.takenotes.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.joey.takenotes.db.Notes
import com.joey.takenotes.db.NotesRoomDatabase
import com.joey.takenotes.repositories.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotesRepository
    val allNotes: LiveData<List<Notes>> // Cache a copy of notes

    init {
        val notesDao = NotesRoomDatabase.getInstance(application).notesDao()
        repository = NotesRepository(notesDao)
        allNotes = repository.allNotes
    }

    private var job = Job()
    private val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun insert(notes: Notes) = scope.launch(Dispatchers.IO) {
        repository.insert(notes)
    }

    fun update(notes: Notes) = scope.launch(Dispatchers.IO) {
        repository.update(notes)
    }

    fun delete(notes: Notes) = scope.launch(Dispatchers.IO) {
        repository.delete(notes)
    }

    fun deleteAllNotes() = scope.launch(Dispatchers.IO) {
        repository.deleteAllNotes()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
