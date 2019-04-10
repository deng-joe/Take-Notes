package com.joey.takenotes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joey.takenotes.R
import com.joey.takenotes.db.Note
import com.joey.takenotes.utils.DateConverter

class NoteAdapter internal constructor(context: Context) : RecyclerView.Adapter<NoteAdapter.NotesViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()  // Cached copy of notes
    private lateinit var listener: NotesClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.model, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = notes[position]
        holder.title.text = currentNote.title
        holder.body.text = currentNote.body
        holder.moment.text = DateConverter.dateFormat(currentNote.date)

        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(notes[holder.adapterPosition])
            }
        }

        holder.itemView.setOnLongClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(notes[holder.adapterPosition])
            }
            true
        }
    }

    fun displayNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }


    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleView)
        val body: TextView = itemView.findViewById(R.id.bodyView)
        val moment: TextView = itemView.findViewById(R.id.dateTime)
    }

    interface NotesClickListener {
        fun onItemClick(note: Note)

        fun onItemLongClick(note: Note)
    }

    fun itemClickListener(listener: NotesClickListener) {
        this.listener = listener
    }
}
