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
import com.joey.takenotes.databinding.ViewModelBinding
import com.joey.takenotes.utils.DateConverter
import java.util.*

class NoteAdapter internal constructor(
    context: Context,
    private val itemClickListener: (Note) -> Unit
) :
    RecyclerView.Adapter<NoteAdapter.NotesViewHolder>(), Filterable {
    private val inflater = LayoutInflater.from(context)
    private var notes = arrayListOf<Note>()  // Cached copy of notes
    var filteredNotes = arrayListOf<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val itemView = inflater.inflate(R.layout.view_model, parent, false)
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

        private val binding = ViewModelBinding.bind(itemView)

        fun bind(note: Note) {
            binding.titleView.text = note.title
            binding.bodyView.text = note.body
            binding.dateTime.text = DateConverter.dateFormat(note.date)

            itemView.setOnClickListener {
                itemClickListener(filteredNotes[adapterPosition])
            }

            itemView.setOnLongClickListener { true }
        }
    }
}
