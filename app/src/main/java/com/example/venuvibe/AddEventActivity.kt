package com.example.venuvibe

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venuvibe.data.EventRepository
import com.example.venuvibe.model.Event
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var btnSubmit: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var eventsRef: DatabaseReference
    private lateinit var repo: EventRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_event)   // now a form

        // wire up views
        etTitle       = findViewById(R.id.eventTitle)
        etDescription = findViewById(R.id.eventDescription)
        etDate        = findViewById(R.id.eventDate)
        btnSubmit     = findViewById(R.id.btnSaveEvent)

        // init Firebase/repo
        database  = FirebaseDatabase.getInstance()
        eventsRef = database.getReference("events")
        repo      = EventRepository(database, eventsRef)

        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc  = etDescription.text.toString().trim()
            val dateStr = etDate.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty() || dateStr.length != 8) {
                Toast.makeText(this, "Fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // parse yyyyMMdd into milliseconds
            val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val dateMs = sdf.parse(dateStr)?.time ?: run {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // build and save
            val newEvent = Event(
                id = "",
                title = title,
                description = desc,
                latitude = 0.0,      // you can hook up a map picker later
                longitude = 0.0,
                date = dateMs,
                averageRating = 0f
            )

            repo.addEvent(newEvent) { error ->
                if (error == null) {
                    Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show()
                    finish()  // return to MainActivity
                } else {
                    Toast.makeText(this, "Save failed: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
