package com.joey.takenotes.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.joey.takenotes.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_note.*

class NewNoteActivity : AppCompatActivity() {
    private lateinit var editTitle: EditText
    private lateinit var editBody: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        editTitle = findViewById(R.id.title)
        editBody = findViewById(R.id.body)

        setBarTitle()
    }

    private fun setBarTitle() {
        val intent = intent
        if (intent.hasExtra(EXTRA_ID)) {
            toolbar.title = "Edit Note"
            editTitle.setText(intent.getStringExtra(EXTRA_TITLE))
            editBody.setText(intent.getStringExtra(EXTRA_BODY))
        } else {
            toolbar.title = "Add Note"
        }
    }

    private fun saveNote() {
        val noteTitle = editTitle.text.toString()
        val noteBody = editBody.text.toString()

        if (TextUtils.isEmpty(noteTitle) || TextUtils.isEmpty(noteBody)) {
            Toasty.info(this, "Please leave no field empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent()
        intent.putExtra(EXTRA_TITLE, noteTitle)
        intent.putExtra(EXTRA_BODY, noteBody)

        val id = 0
        intent.putExtra(EXTRA_ID, id)

        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == R.id.save) {
                saveNote()
                return true
            } else if (item.itemId == R.id.discard) {
                Toasty.info(this, "Note discarded.", Toast.LENGTH_SHORT).show()
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_ID = "com.joey.takenotes.ui.EXTRA_ID"
        const val EXTRA_TITLE = "com.joey.takenotes.ui.EXTRA_TITLE"
        const val EXTRA_BODY = "com.joey.takenotes.ui.EXTRA_BODY"
    }
}
