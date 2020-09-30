package com.joey.takenotes.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.joey.takenotes.R
import com.joey.takenotes.adapters.NoteAdapter
import com.joey.takenotes.data.Note
import com.joey.takenotes.data.NoteRoomDatabase
import com.joey.takenotes.databinding.ActivityMainBinding
import com.joey.takenotes.utils.SwipeToDeleteCallback
import com.joey.takenotes.viewmodels.NoteViewModel
import es.dmoral.toasty.Toasty
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SwipeToDeleteCallback.NoteItemTouchHelperListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    private val onItemClickListener: (Note) -> Unit = { note ->
        val intent = Intent(this, NewNoteActivity::class.java).apply {
            putExtra(NewNoteActivity.EXTRA_ID, note.id)
            putExtra(NewNoteActivity.EXTRA_TITLE, note.title)
            putExtra(NewNoteActivity.EXTRA_BODY, note.body)
        }
        startActivityForResult(intent, RC_EDIT_NOTE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

        addItemTouchListener()
    }

    private fun initViews() {
        binding.fab.setOnClickListener {
            val intent = Intent(this, NewNoteActivity::class.java)
            startActivityForResult(intent, RC_ADD_NOTE)
        }

        noteAdapter = NoteAdapter(this, onItemClickListener)
        binding.notesRecyclerView.apply {
            setHasFixedSize(true)
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        // Get a new or existing ViewModel from the ViewModelProviders
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        // Add an Observer class on the LiveData
        noteViewModel.allNotes.observe(this, { notes ->
            // Update the cached copy of the notes in the adapter
            if (notes.isNotEmpty()) {
                binding.emptyView.visibility = View.GONE
                noteAdapter.apply {
                    addNotes(notes as ArrayList<Note>)
                    displayNotes(notes)
                }
            } else {
                binding.emptyView.visibility = View.VISIBLE
            }
        })
    }

    private fun addItemTouchListener() {
        val callback = SwipeToDeleteCallback(
            this,
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT,
            this
        )
        ItemTouchHelper(callback).attachToRecyclerView(binding.notesRecyclerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        NoteRoomDatabase.destroyInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.queryHint = getString(R.string.search_notes)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                noteAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                noteAdapter.filter.filter(newText)
                return false
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.del) {
            if (noteAdapter.itemCount == 0) {
                Toasty.info(this, getString(R.string.no_notes_to_delete), Toast.LENGTH_SHORT).show()
                return false
            }

            AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirmation))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    noteViewModel.deleteAllNotes()
                    noteAdapter.clearData()
                    Toasty.success(this, getString(R.string.all_notes_deleted), Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val createdOn: Date = Calendar.getInstance().time

        if (data != null) {
            if (requestCode == RC_ADD_NOTE && resultCode == Activity.RESULT_OK) {
                val note = Note(
                    data.getStringExtra(NewNoteActivity.EXTRA_TITLE)!!,
                    data.getStringExtra(NewNoteActivity.EXTRA_BODY)!!,
                    createdOn
                )
                noteViewModel.insert(note)
                Toasty.success(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
            } else if (requestCode == RC_EDIT_NOTE && resultCode == Activity.RESULT_OK) {
                val noteId = data.getIntExtra(NewNoteActivity.EXTRA_ID, -1)
                if (noteId == -1) {
                    Toasty.error(this, getString(R.string.update_failed), Toast.LENGTH_SHORT).show()
                    return
                }
                val note = Note(
                    data.getStringExtra(NewNoteActivity.EXTRA_TITLE)!!,
                    data.getStringExtra(NewNoteActivity.EXTRA_BODY)!!,
                    createdOn
                )
                note.id = noteId
                noteViewModel.update(note)
                Toasty.success(this, getString(R.string.updated), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is NoteAdapter.NotesViewHolder) {
            // Note to be swiped
            val deletedNote = noteAdapter.filteredNotes[viewHolder.bindingAdapterPosition]

            // Position of the note to be swiped
            val deletedIndex = viewHolder.bindingAdapterPosition

            // Remove note from RecyclerView
            noteAdapter.removeNote(viewHolder.bindingAdapterPosition)

            // Show Snackbar with option to undo note removal
            Snackbar.make(binding.coordinator, getString(R.string.deleted), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.undo)) {
                    // Restore the deleted note
                    noteAdapter.restoreNote(deletedNote, deletedIndex)
                }
                .addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            // Delete note from database
                            noteViewModel.delete(deletedNote)
                        }
                    }
                })
                .show()
        }
    }

    companion object {
        const val RC_ADD_NOTE = 1
        const val RC_EDIT_NOTE = 2
    }
}
