package com.joey.takenotes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joey.takenotes.R
import com.joey.takenotes.data.Note
import com.joey.takenotes.utils.DateConverter
import java.util.*

class NoteAdapter internal constructor(context: Context) :
    RecyclerView.Adapter<NoteAdapter.NotesViewHolder>(), Filterable {
    private val inflater = LayoutInflater.from(context)
    private var notes = emptyList<Note>()  // Cached copy of notes
    private var filteredNotes = emptyList<Note>()
    private lateinit var listener: NotesClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.model, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun getItemCount() = filteredNotes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNote = filteredNotes[position]
        holder.title.text = currentNote.title
        holder.body.text = currentNote.body
        holder.moment.text = DateConverter.dateFormat(currentNote.date)

        holder.itemView.setOnClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(filteredNotes[holder.adapterPosition])
            }
        }

        holder.itemView.setOnLongClickListener {
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemLongClick(filteredNotes[holder.adapterPosition])
            }
            true
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charString = constraint.toString()
                if (charString.isEmpty()) {
                    filteredNotes = notes
                } else {
                    val filteredList = arrayListOf<Note>()
                    for (row in notes) {
                        if (row.title.toLowerCase(Locale.getDefault())
                                .contains(charString.toLowerCase(Locale.getDefault())) ||
                            row.body.toLowerCase(Locale.getDefault())
                                .contains(charString.toLowerCase(Locale.getDefault()))
                        )
                            filteredList.add(row)
                    }
                    filteredNotes = filteredList
                }

                val results = FilterResults()
                results.values = filteredNotes
                return results
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                filteredNotes = results?.values as List<Note>
                notifyDataSetChanged()
            }
        }
    }

    fun displayNotes(notes: List<Note>) {
        this.notes = notes
        this.filteredNotes = notes
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
