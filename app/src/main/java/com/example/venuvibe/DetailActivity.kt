package com.example.venuvibe

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.venuvibe.model.Event
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.*

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var mapView: MapView
    private lateinit var detailTitle: TextView
    private lateinit var detailDate: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnSaveRating: Button

    private lateinit var eventsRef: DatabaseReference
    private var googleMap: GoogleMap? = null
    private var loadedEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // 1) bind views
        toolbar              = findViewById(R.id.toolbar)
        detailTitle          = findViewById(R.id.detailTitle)
        detailDate           = findViewById(R.id.detailDate)
        tvDetailDescription  = findViewById(R.id.tvDetailDescription)
        ratingBar            = findViewById(R.id.ratingBar)
        btnSaveRating        = findViewById(R.id.btnSaveRating)
        mapView              = findViewById(R.id.mapView)

        // 2) set up toolbar as action bar, enable “up”
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // 3) hide rating controls until we know this is a past event
        ratingBar.visibility    = View.GONE
        btnSaveRating.visibility = View.GONE

        // 4) grab eventId from intent
        val eventId = intent.getStringExtra("eventId")
            ?: run {
                Toast.makeText(this, "No event ID provided", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        // 5) point at the right node
        eventsRef = FirebaseDatabase
            .getInstance()
            .getReference("events")
            .child(eventId)

        // 6) fetch event once
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event = snapshot.getValue(Event::class.java)
                if (event == null) {
                    Toast.makeText(this@DetailActivity, "Event not found", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }
                loadedEvent = event

                // bind text
                detailTitle.text         = event.title
                detailDate.text          = event.date.getDateFormatted()
                tvDetailDescription.text = event.description

                // rating UI only for past events
                if (event.date < System.currentTimeMillis()) {
                    ratingBar.visibility    = View.VISIBLE
                    btnSaveRating.visibility = View.VISIBLE
                    ratingBar.rating         = event.averageRating

                    btnSaveRating.setOnClickListener {
                        val newRating = ratingBar.rating
                        eventsRef.child("averageRating")
                            .setValue(newRating)
                            .addOnSuccessListener {
                                Toast.makeText(this@DetailActivity,
                                    "Rating updated!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { err ->
                                Toast.makeText(this@DetailActivity,
                                    "Failed to save rating: ${err.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }

                // if the map is already ready, drop the pin now
                googleMap?.let { showMarkerOnMap(it, event) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailActivity,
                    "Failed to load event: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        // 7) initialize the MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // if we already fetched the event, place the marker; otherwise onDataChange will
        loadedEvent?.let { showMarkerOnMap(map, it) }
    }

    private fun showMarkerOnMap(map: GoogleMap, event: Event) {
        val pos = LatLng(event.latitude, event.longitude)
        map.clear()
        map.addMarker(MarkerOptions().position(pos).title(event.title))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
    }

    // forward lifecycle calls for MapView
    override fun onResume()           { super.onResume(); mapView.onResume() }
    override fun onStart()            { super.onStart();  mapView.onStart() }
    override fun onPause()            { mapView.onPause(); super.onPause() }
    override fun onStop()             { super.onStop();   mapView.onStop() }
    override fun onDestroy()          { mapView.onDestroy(); super.onDestroy() }
    override fun onLowMemory()        { super.onLowMemory(); mapView.onLowMemory() }
    override fun onSaveInstanceState(out: Bundle) {
        super.onSaveInstanceState(out)
        mapView.onSaveInstanceState(out)
    }
}

// formatting extension
fun Long.getDateFormatted(): String {
    val fmt = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return fmt.format(java.util.Date(this))
}
