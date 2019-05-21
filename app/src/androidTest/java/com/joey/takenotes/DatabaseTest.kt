package com.joey.takenotes

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.joey.takenotes.db.Note
import com.joey.takenotes.db.NoteDao
import com.joey.takenotes.db.NoteRoomDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.*

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var noteDao: NoteDao
    private lateinit var noteRoomDatabase: NoteRoomDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().context
        noteRoomDatabase = Room.inMemoryDatabaseBuilder(context, NoteRoomDatabase::class.java).build()
        noteDao = noteRoomDatabase.noteDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        noteRoomDatabase.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeAndReadData() {
        val date = Date()
        val note = Note("title", "body", date)
        noteDao.insert(note)
    }
}