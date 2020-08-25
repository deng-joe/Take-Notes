package com.joey.takenotes.ui

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
import com.joey.takenotes.utils.SwipeToDeleteCallback
import com.joey.takenotes.viewmodels.NoteViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SwipeToDeleteCallback.NoteItemTouchHelperListener {
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    private val onItemClickListener: (Note) -> Unit = { note ->
        val intent = Intent(this, NewNoteActivity::class.java)
        intent.putExtra(NewNoteActivity.EXTRA_ID, note.id)
        intent.putExtra(NewNoteActivity.EXTRA_TITLE, note.title)
        intent.putExtra(NewNoteActivity.EXTRA_BODY, note.body)
        startActivityForResult(intent, RC_EDIT_NOTE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this, NewNoteActivity::class.java)
            startActivityForResult(intent, RC_ADD_NOTE)
        }

        initViews()

        addItemTouchListener()
    }

    private fun initViews() {
        noteAdapter = NoteAdapter(this, onItemClickListener)
        notes_recycler_view.adapter = noteAdapter
        notes_recycler_view.layoutManager = LinearLayoutManager(this)

        // Get a new or existing ViewModel from the ViewModelProviders
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        // Add an Observer class on the LiveData
        noteViewModel.allNotes.observe(this, { notes ->
            // Update the cached copy of the notes in the adapter
            if (notes.isNotEmpty()) {
                empty_view.visibility = View.GONE
                noteAdapter.displayNotes(notes as ArrayList<Note>)
            } else {
                empty_view.visibility = View.VISIBLE
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
        ItemTouchHelper(callback).attachToRecyclerView(notes_recycler_view)
    }

    override fun onDestroy() {
        super.onDestroy()
        NoteRoomDatabase.destroyInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.queryHint = "Search your notes"
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.del) {
            if (noteAdapter.itemCount == 0) {
                Toasty.info(this, "There are no notes to delete.", Toast.LENGTH_SHORT).show()
                return false
            }

            AlertDialog.Builder(this)
                .setMessage("Delete all notes?")
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->
                    noteViewModel.deleteAllNotes()
                    Toasty.success(this, "All notes deleted.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
            return true
        }

        return super.onOptionsItemSelected(item!!)
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
                Toasty.success(this, "Note saved.", Toast.LENGTH_SHORT).show()
            } else if (requestCode == RC_EDIT_NOTE && resultCode == Activity.RESULT_OK) {
                val noteId = data.getIntExtra(NewNoteActivity.EXTRA_ID, -1)
                if (noteId == -1) {
                    Toasty.error(this, "Failed to update note.", Toast.LENGTH_SHORT).show()
                    return
                }
                val note = Note(
                    data.getStringExtra(NewNoteActivity.EXTRA_TITLE)!!,
                    data.getStringExtra(NewNoteActivity.EXTRA_BODY)!!,
                    createdOn
                )
                note.id = noteId
                noteViewModel.update(note)
                Toasty.success(this, "Note updated.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is NoteAdapter.NotesViewHolder) {
            // Note to be swiped
            val deletedNote = noteAdapter.filteredNotes[viewHolder.adapterPosition]
            // Position of the note to be swiped
            val deletedIndex = viewHolder.adapterPosition

            // Remove note from RecyclerView
            noteAdapter.removeNote(viewHolder.adapterPosition)

            // Show Snackbar with option to undo note removal
            Snackbar.make(coordinator, "Note deleted.", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
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
