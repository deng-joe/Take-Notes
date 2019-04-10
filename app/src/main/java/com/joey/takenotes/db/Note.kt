package com.joey.takenotes.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "take_notes")
data class Note(var title: String, var body: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
