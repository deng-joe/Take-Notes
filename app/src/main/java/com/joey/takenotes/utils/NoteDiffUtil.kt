package com.joey.takenotes.utils

import androidx.recyclerview.widget.DiffUtil
import com.joey.takenotes.data.Note

/**
 * Created by Joe on 9/30/2020.
 */
class NoteDiffUtil(
    private val oldNotes: List<Note>,
    private val newNotes: List<Note>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldNotes.size

    override fun getNewListSize() = newNotes.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNotes[oldItemPosition].id == newNotes[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNotes[oldItemPosition] == newNotes[newItemPosition]
    }
}
