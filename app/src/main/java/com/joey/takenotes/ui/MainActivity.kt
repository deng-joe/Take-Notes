package com.joey.takenotes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.joey.takenotes.R
import com.joey.takenotes.adapters.NoteAdapter
import com.joey.takenotes.db.Note
import com.joey.takenotes.viewmodels.NoteViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewNoteActivity::class.java)
            startActivityForResult(intent, RC_ADD_NOTE)
        }

        initUI()
    }

    private fun initUI() {
        noteAdapter = NoteAdapter(this)
        notes_view.adapter = noteAdapter
        notes_view.layoutManager = LinearLayoutManager(this)

        // Get a new or existing ViewModel from the ViewModelProviders
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        // Add an Observer class on the LiveData
        noteViewModel.allNotes.observe(this, Observer<List<Note>> {
            // Update the cached copy of the notes in the adapter
            noteAdapter.displayNotes(it)
        })

        noteAdapter.itemClickListener(object : NoteAdapter.NotesClickListener {
            override fun onItemClick(note: Note) {
                val intent = Intent(this@MainActivity, NewNoteActivity::class.java)
                intent.putExtra(NewNoteActivity.EXTRA_ID, note.id)
                intent.putExtra(NewNoteActivity.EXTRA_TITLE, note.title)
                intent.putExtra(NewNoteActivity.EXTRA_BODY, note.body)
                startActivityForResult(intent, RC_EDIT_NOTE)
            }

            override fun onItemLongClick(note: Note) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("Delete this note?")
                builder.setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                builder.setPositiveButton("Yes") { _, _ ->
                    noteViewModel.delete(note)
                    Toasty.success(this@MainActivity, "Note deleted.", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.del) {
            if (noteAdapter.itemCount == 0) {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "There are no notes to delete.",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Delete all notes?")
                builder.setCancelable(false)
                builder.setPositiveButton("OK") { _, _ ->
                    noteViewModel.deleteAllNotes()
                    Toasty.info(this, "All notes deleted.", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val createdOn: Date = Calendar.getInstance().time

        if (requestCode == RC_ADD_NOTE && resultCode == Activity.RESULT_OK) {
            val note = Note(
                data!!.getStringExtra(NewNoteActivity.EXTRA_TITLE),
                data.getStringExtra(NewNoteActivity.EXTRA_BODY),
                createdOn
            )
            noteViewModel.insert(note)
            Toasty.success(this, "Note saved.", Toast.LENGTH_SHORT).show()
        } else if (requestCode == RC_EDIT_NOTE && resultCode == Activity.RESULT_OK) {
            val noteId = data?.getIntExtra(NewNoteActivity.EXTRA_ID, -1)
            if (noteId == -1) {
                Toasty.error(this, "Failed to update note.", Toast.LENGTH_SHORT).show()
                return
            }
            val note = Note(
                data!!.getStringExtra(NewNoteActivity.EXTRA_TITLE),
                data.getStringExtra(NewNoteActivity.EXTRA_BODY),
                createdOn
            )
            note.id = data.getIntExtra(NewNoteActivity.EXTRA_ID, -1)
            noteViewModel.update(note)
            Toasty.success(this, "Note updated.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val RC_ADD_NOTE = 1
        const val RC_EDIT_NOTE = 2
    }
}
