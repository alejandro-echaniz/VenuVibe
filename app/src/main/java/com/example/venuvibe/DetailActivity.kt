package com.example.venuvibe

// test comment to push

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.venuvibe.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var event: Event
    private lateinit var detailTitle: TextView
    private lateinit var imgEvent: ImageView
    private lateinit var detailDate: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSaveRating: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // Initialize UI elements
        detailTitle = findViewById(R.id.detailTitle)
        imgEvent = findViewById(R.id.imgEvent)
        detailDate = findViewById(R.id.detailDate)
        tvDetailDescription = findViewById(R.id.tvDetailDescription)
        ratingBar = findViewById(R.id.ratingBar)
        btnSaveRating = findViewById(R.id.btnSaveRating)

        // Get the event from the Intent
        event = intent.getSerializableExtra("event") as Event

        // Bind event data to views
        detailTitle.text = event.title
        imgEvent.setImageResource(R.drawable.img)  // Replace with actual image if available
        detailDate.text = event.date.getDateFormatted() // Adjust format as necessary
        tvDetailDescription.text = event.description

        // Save rating
        btnSaveRating.setOnClickListener {
            val rating = ratingBar.rating
            // Save rating to Firebase or local storage here
        }
    }

    fun Long.getDateFormatted(): String {
        val date = Date(this) // Convert the timestamp (Long) to a Date object
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Define the format
        return format.format(date) // Return the formatted date as a String
    }
}
