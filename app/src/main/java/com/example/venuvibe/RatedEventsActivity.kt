package com.example.venuvibe
import android.content.Intent

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.venuvibe.model.Event
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RatedEventsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var ratedEventAdapter: EventAdapter
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var ratedEventsList: MutableList<Event> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rated_events)

        // Initialize UI elements
        recyclerView = findViewById(R.id.rvRatedEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance()

        // Setup the adapter for the RecyclerView
        ratedEventAdapter = EventAdapter(ratedEventsList) { event ->
            // On item click, navigate to the detail activity
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("event", event)
            startActivity(intent)
        }
        recyclerView.adapter = ratedEventAdapter

        // Fetch rated events from Firebase
        fetchRatedEventsFromFirebase()
    }

    private fun fetchRatedEventsFromFirebase() {
        val eventsRef = firebaseDatabase.getReference("events")

        // Listen for changes to the events node
        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ratedEventsList.clear() // Clear the list to avoid duplicates

                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)

                    // Only add events that have ratings
                    if (event != null && event.averageRating != null) {
                        ratedEventsList.add(event)
                    }
                }

                // Update the RecyclerView with the rated events
                ratedEventAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RatedEventsActivity, "Failed to load rated events.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
