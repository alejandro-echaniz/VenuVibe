package com.example.venuvibe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.venuvibe.R
import com.example.venuvibe.model.Event

class EventAdapter(
    private var events: List<Event>,
    private val onClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    fun updateEvents(newEvents: List<Event>) {
        events = newEvents
        notifyDataSetChanged()
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        private val tvDate: TextView = itemView.findViewById(R.id.tvEventDate)
        // private val tvDescription: TextView = itemView.findViewById(R.id.eventDescription)
        // private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)

        fun bind(event: Event) {
            tvTitle.text = event.title
            tvDate.text = event.getDateFormatted() // if you format date in model
            //tvDescription.text = event.description
            //ratingBar.rating = event.averageRating

            itemView.setOnClickListener { onClick(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event_added, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount() = events.size
}
