package com.joey.takenotes.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.joey.takenotes.R
import com.joey.takenotes.data.Note
import com.joey.takenotes.utils.DateConverter
import kotlinx.android.synthetic.main.model.view.*
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter internal constructor(
    context: Context,
    private val itemClickListener: (Note) -> Unit
) :
    RecyclerView.Adapter<NoteAdapter.NotesViewHolder>(), Filterable {
    private val inflater = LayoutInflater.from(context)
    private var notes = arrayListOf<Note>()  // Cached copy of notes
    var filteredNotes = arrayListOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.model, parent, false)
        return NotesViewHolder(itemView)
    }

    override fun getItemCount() = filteredNotes.size

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) =
        holder.bind(filteredNotes[position])

    @Suppress("UNCHECKED_CAST")
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val charString = constraint.toString()
                filteredNotes = if (charString.isEmpty()) {
                    notes
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
                    filteredList
                }

                val results = FilterResults()
                results.values = filteredNotes
                return results
            }

            override fun publishResults(charSequence: CharSequence?, results: FilterResults?) {
                filteredNotes = results?.values as ArrayList<Note>
                notifyDataSetChanged()
            }
        }
    }

    fun displayNotes(notes: ArrayList<Note>) {
        this.notes = notes
        this.filteredNotes = notes
        notifyDataSetChanged()
    }

    fun removeNote(position: Int) {
        filteredNotes.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreNote(note: Note, position: Int) {
        filteredNotes.add(position, note)
        notifyItemInserted(position)
    }

    fun clearData() {
        filteredNotes.clear()
        notifyDataSetChanged()
    }

    inner class NotesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(note: Note) {
            itemView.titleView.text = note.title
            itemView.bodyView.text = note.body
            itemView.dateTime.text = DateConverter.dateFormat(note.date)

            itemView.setOnClickListener {
                itemClickListener(filteredNotes[adapterPosition])
            }

            itemView.setOnLongClickListener { true }
        }
    }
}
