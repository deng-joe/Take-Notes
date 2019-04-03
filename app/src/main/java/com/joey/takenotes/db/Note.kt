package com.joey.takenotes.db

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "take_notes")
data class Note(@NonNull var title: String, @NonNull var body: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
