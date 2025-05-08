package com.example.venuvibe

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venuvibe.data.EventRepository
import com.example.venuvibe.model.Event
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : AppCompatActivity() {
    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var etTime: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSubmit: Button

    private lateinit var database: FirebaseDatabase
    private lateinit var eventsRef: DatabaseReference
    private lateinit var repo: EventRepository

    // holds the user-picked hour & minute
    private var selectedHour = 0
    private var selectedMinute = 0

    // holds the user-picked location
    private var selectedLatLng: LatLng? = null
    private var selectedAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_event)

        // 1) bind views
        etTitle       = findViewById(R.id.eventTitle)
        etDescription = findViewById(R.id.eventDescription)
        datePicker    = findViewById(R.id.datePicker)
        etTime        = findViewById(R.id.eventTime)
        ratingBar     = findViewById(R.id.ratingBar)
        btnSubmit     = findViewById(R.id.btnSaveEvent)

        // hide rating bar by default
        ratingBar.visibility = View.GONE

        // init Firebase & repo
        database  = FirebaseDatabase.getInstance()
        eventsRef = database.getReference("events")
        repo      = EventRepository(database, eventsRef)

        // 2) default date to today, and listen for changes
        Calendar.getInstance().also { today ->
            datePicker.init(
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            ) { _, _, _, _ ->
                updateRatingVisibility()
            }
        }

        // 3) set up Places autocomplete fragment
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.eventLocation) as AutocompleteSupportFragment

        autocompleteFragment.setHint("Enter event location")
        autocompleteFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedLatLng  = place.latLng
                selectedAddress = place.address ?: place.name.orEmpty()
            }
            override fun onError(status: Status) {
                Toast.makeText(
                    this@AddEventActivity,
                    "Error selecting place: $status",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // 4) wire up the dial-style time picker
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
                now.set(Calendar.MINUTE, selectedMinute)
                val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
                etTime.setText(fmt.format(now.time))
                updateRatingVisibility()
            }

            picker.show(supportFragmentManager, "TIME_PICKER")
        }

        // 5) handle form submission
        btnSubmit.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc  = etDescription.text.toString().trim()
            val time  = etTime.text.toString().trim()

            if (title.isEmpty()
                || desc.isEmpty()
                || time.isEmpty()
                || selectedLatLng == null
                || selectedAddress.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill all fields and select a location",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // combine date + time into one timestamp
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR,        datePicker.year)
                set(Calendar.MONTH,       datePicker.month)
                set(Calendar.DAY_OF_MONTH,datePicker.dayOfMonth)
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE,      selectedMinute)
                set(Calendar.SECOND,      0)
                set(Calendar.MILLISECOND, 0)
            }
            val timestamp = cal.timeInMillis

            // build and save the new event
            val newEvent = Event(
                id            = "",
                title         = title,
                description   = "$desc\nLocation: $selectedAddress",
                latitude      = selectedLatLng!!.latitude,
                longitude     = selectedLatLng!!.longitude,
                date          = timestamp,
                averageRating = 0f
            )

            repo.addEvent(newEvent) { err ->
                if (err == null) {
                    Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Save failed: ${err.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Show the RatingBar only if the picked date+time is before now.
     */
    private fun updateRatingVisibility() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR,        datePicker.year)
            set(Calendar.MONTH,       datePicker.month)
            set(Calendar.DAY_OF_MONTH,datePicker.dayOfMonth)
            set(Calendar.HOUR_OF_DAY, selectedHour)
            set(Calendar.MINUTE,      selectedMinute)
            set(Calendar.SECOND,      0)
            set(Calendar.MILLISECOND, 0)
        }
        ratingBar.visibility =
            if (cal.timeInMillis < System.currentTimeMillis())
                View.VISIBLE
            else
                View.GONE
    }
}
