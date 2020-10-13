package com.joey.takenotes.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.joey.takenotes.data.Note
import com.joey.takenotes.data.NoteRoomDatabase
import com.joey.takenotes.repositories.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Used to provide data to the UI and survive configuration changes.
 */
class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>> // Cache a copy of notes

    init {
        val noteDao = NoteRoomDatabase.getInstance(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
    }

    /**
     * Launch a new coroutine to insert, update or delete the notes in a non-blocking way.
     */

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun deleteAllNotes() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllNotes()
    }
}
