package com.joey.takenotes.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.joey.takenotes.data.Note
import com.joey.takenotes.data.NoteRoomDatabase
import com.joey.takenotes.repositories.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>> // Cache a copy of notes

    init {
        val noteDao = NoteRoomDatabase.getInstance(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
    }

    private var job = Job()
    private val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)

    fun insert(note: Note) = scope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: Note) = scope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun delete(note: Note) = scope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun deleteAllNotes() = scope.launch(Dispatchers.IO) {
        repository.deleteAllNotes()
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}
