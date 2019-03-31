package com.joey.takenotes.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(val title: String, val body: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
