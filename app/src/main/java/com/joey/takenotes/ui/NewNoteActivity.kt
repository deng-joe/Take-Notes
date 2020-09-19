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
import com.joey.takenotes.databinding.ActivityNewNoteBinding
import es.dmoral.toasty.Toasty

class NewNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBarTitle()
    }

    private fun setBarTitle() {
        val intent = intent
        if (intent != null && intent.hasExtra(EXTRA_ID)) {
            supportActionBar?.title = getString(R.string.update_note)
            binding.header.setText(intent.getStringExtra(EXTRA_TITLE))
            binding.body.setText(intent.getStringExtra(EXTRA_BODY))
        } else {
            supportActionBar?.title = getString(R.string.add_note)
        }
    }

    private fun saveNote() {
        val noteTitle = binding.header.text.toString()
        val noteBody = binding.body.text.toString()

        if (TextUtils.isEmpty(noteTitle) || TextUtils.isEmpty(noteBody)) {
            Toasty.error(this, getString(R.string.invalid), Toast.LENGTH_SHORT).show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            saveNote()
            return true
        } else if (item.itemId == R.id.discard) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_ID = "com.joey.takenotes.ui.EXTRA_ID"
        const val EXTRA_TITLE = "com.joey.takenotes.ui.EXTRA_TITLE"
        const val EXTRA_BODY = "com.joey.takenotes.ui.EXTRA_BODY"
    }
}
