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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.joey.takenotes.R
import com.joey.takenotes.adapters.NoteAdapter
import com.joey.takenotes.db.NoteEntity
import com.joey.takenotes.viewmodels.NoteViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*

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
        val recyclerView = findViewById<RecyclerView>(R.id.notes_view)
        noteAdapter = NoteAdapter(this)
        recyclerView.adapter = noteAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get a new or existing ViewModel from the ViewModelProviders
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        // Add an Observer class on the LiveData
        noteViewModel.allNotes.observe(this, Observer { notes ->
            // Update the cached copy of the notes in the adapter
            notes?.let {
                noteAdapter.displayNotes(it)
            }
        })

        noteAdapter.itemClickListener(object : NoteAdapter.NotesClickListener {
            override fun onItemClick(note: NoteEntity) {
                super.onItemClick(note)
                val intent = Intent(this@MainActivity, NewNoteActivity::class.java)
                intent.putExtra(NewNoteActivity.EXTRA_ID, note.id)
                intent.putExtra(NewNoteActivity.EXTRA_TITLE, note.title)
                intent.putExtra(NewNoteActivity.EXTRA_BODY, note.body)
                startActivityForResult(intent, RC_EDIT_NOTE)
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

        if (requestCode == RC_ADD_NOTE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val note = NoteEntity(
                    it.getStringExtra(NewNoteActivity.EXTRA_TITLE),
                    it.getStringExtra(NewNoteActivity.EXTRA_BODY)
                )
                noteViewModel.insert(note)
                Toasty.success(this, "Note saved.", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == RC_EDIT_NOTE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val note = NoteEntity(
                    it.getStringExtra(NewNoteActivity.EXTRA_TITLE),
                    it.getStringExtra(NewNoteActivity.EXTRA_BODY)
                )
                noteViewModel.update(note)
                Toasty.success(this, "Note updated.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val RC_ADD_NOTE = 1
        const val RC_EDIT_NOTE = 2
    }
}
