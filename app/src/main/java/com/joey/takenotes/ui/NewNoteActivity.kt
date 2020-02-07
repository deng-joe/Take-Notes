package com.joey.takenotes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.joey.takenotes.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_new_note.*

class NewNoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        setBarTitle()
    }

    private fun setBarTitle() {
        val intent = intent
        if (intent != null && intent.hasExtra(EXTRA_ID)) {
            supportActionBar?.title = "Edit Note"
            header.setText(intent.getStringExtra(EXTRA_TITLE))
            body.setText(intent.getStringExtra(EXTRA_BODY))
        } else {
            supportActionBar?.title = "Add Note"
        }
    }

    private fun saveNote() {
        val noteTitle = header.text.toString()
        val noteBody = body.text.toString()

        if (TextUtils.isEmpty(noteTitle) || TextUtils.isEmpty(noteBody)) {
            Toasty.error(this, "Please leave no field empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent().apply {
            putExtra(EXTRA_TITLE, noteTitle)
            putExtra(EXTRA_BODY, noteBody)

            if (intent.getIntExtra(EXTRA_ID, -1) != -1) {
                putExtra(EXTRA_ID, intent.getIntExtra(EXTRA_ID, -1))
            }
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.save) {
            saveNote()
            return true
        } else if (item?.itemId == R.id.discard) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item!!)
    }

    companion object {
        const val EXTRA_ID = "com.joey.takenotes.ui.EXTRA_ID"
        const val EXTRA_TITLE = "com.joey.takenotes.ui.EXTRA_TITLE"
        const val EXTRA_BODY = "com.joey.takenotes.ui.EXTRA_BODY"
    }
}
