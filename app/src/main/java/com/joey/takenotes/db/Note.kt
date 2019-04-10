package com.joey.takenotes.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "take_notes")
data class Note(var title: String, var body: String, var date: Date) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
