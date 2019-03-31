package com.joey.takenotes.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(val title: String, val text: String) {
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
}
