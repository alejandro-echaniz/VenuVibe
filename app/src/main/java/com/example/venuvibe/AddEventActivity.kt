package com.example.venuvibe

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venuvibe.data.EventRepository
import com.example.venuvibe.model.Event
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var etTime: EditText
    private lateinit var btnSubmit: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var eventsRef: DatabaseReference
    private lateinit var repo: EventRepository

    // holds the user‐picked hour & minute
    private var selectedHour = 0
    private var selectedMinute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_event)

        // bind views
        etTitle       = findViewById(R.id.eventTitle)
        etDescription = findViewById(R.id.eventDescription)
        datePicker    = findViewById(R.id.datePicker)
        etTime        = findViewById(R.id.eventTime)
        btnSubmit     = findViewById(R.id.btnSaveEvent)

        // init Firebase & repo
        database  = FirebaseDatabase.getInstance()
        eventsRef = database.getReference("events")
        repo      = EventRepository(database, eventsRef)

        // set the DatePicker to today by default
        val today = Calendar.getInstance()
        datePicker.init(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH),
            null
        )

        // wire up the dial‐style time picker
        etTime.setOnClickListener {
            val now = Calendar.getInstance()
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(now.get(Calendar.HOUR_OF_DAY))
                .setMinute(now.get(Calendar.MINUTE))
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .build()

            picker.addOnPositiveButtonClickListener {
                selectedHour   = picker.hour
                selectedMinute = picker.minute
                now.set(Calendar.HOUR_OF_DAY, selectedHour)
                now.set(Calendar.MINUTE,        selectedMinute)
                val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
                etTime.setText(fmt.format(now.time))
            }

            picker.show(supportFragmentManager, "TIME_PICKER")
        }

        // handle form submission
        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc  = etDescription.text.toString().trim()
            val time  = etTime.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty() || time.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // build a Calendar combining date + time
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR,          datePicker.year)
                set(Calendar.MONTH,         datePicker.month)
                set(Calendar.DAY_OF_MONTH,  datePicker.dayOfMonth)
                set(Calendar.HOUR_OF_DAY,   selectedHour)
                set(Calendar.MINUTE,        selectedMinute)
                set(Calendar.SECOND,        0)
                set(Calendar.MILLISECOND,   0)
            }
            val timestamp = cal.timeInMillis

            // create and save the event
            val newEvent = Event(
                id            = "",
                title         = title,
                description   = desc,
                latitude      = 0.0,
                longitude     = 0.0,
                date          = timestamp,
                averageRating = 0f
            )

            repo.addEvent(newEvent) { err ->
                if (err == null) {
                    Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Save failed: ${err.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
