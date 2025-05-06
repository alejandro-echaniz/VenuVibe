package com.example.venuvibe

// test comment to push

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venuvibe.data.EventRepository
import com.example.venuvibe.model.Event
import com.google.firebase.database.FirebaseDatabase

class AddEventActivity : AppCompatActivity() {

    private lateinit var eventTitle: EditText
    private lateinit var eventDescription: EditText
    private lateinit var eventDate: EditText
    private lateinit var btnSaveEvent: Button
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var eventRepository: EventRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_event)

        // Initialize Firebase database
        firebaseDatabase = FirebaseDatabase.getInstance()
        eventRepository = EventRepository(firebaseDatabase, firebaseDatabase.getReference("events"))

        // Bind UI elements
        eventTitle = findViewById(R.id.eventTitle)
        eventDescription = findViewById(R.id.eventDescription)
        eventDate = findViewById(R.id.eventDate)


        // Save event to Firebase when button is clicked
        btnSaveEvent.setOnClickListener {
            val title = eventTitle.text.toString()
            val description = eventDescription.text.toString()
            val date = eventDate.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty()) {
                val event = Event(title, description, date)  // Use proper date format
                eventRepository.addEvent(event) { error ->
                    if (error != null) {
                        Toast.makeText(this, "Error saving event: ${error.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show()
                        finish()  // Close the activity
                    }
                }
            } else {
                Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
