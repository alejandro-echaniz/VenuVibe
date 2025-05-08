package com.example.venuvibe

import android.os.Bundle
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

class DetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var event: Event
    private lateinit var mapView: MapView
    private lateinit var detailTitle: TextView
    private lateinit var detailDate: TextView
    private lateinit var tvDetailDescription: TextView
    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // 1) Bind your views
        detailTitle         = findViewById(R.id.detailTitle)
        detailDate          = findViewById(R.id.detailDate)
        tvDetailDescription = findViewById(R.id.tvDetailDescription)
        ratingBar           = findViewById(R.id.ratingBar)
        mapView             = findViewById(R.id.mapView)

        // 2) Pull the Parcelable Event out of the Intent into your property
        event = intent.getParcelableExtra<Event>("event")
            ?: run {
                Toast.makeText(this, "No event data!", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        // 3) Immediately bind all the text fields
        detailTitle.text         = event.title
        detailDate.text          = event.date.getDateFormatted()
        tvDetailDescription.text = event.description
        ratingBar.rating         = event.averageRating

        // 4) Initialize and request the map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    // 5) Once the map is ready, drop your pin at event.latitude/longitude
    override fun onMapReady(googleMap: GoogleMap) {
        val pos = LatLng(event.latitude, event.longitude)
        googleMap.addMarker(MarkerOptions().position(pos).title(event.title))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
    }

    // 6) Forward MapView lifecycle calls
    override fun onResume()           { super.onResume();    mapView.onResume() }
    override fun onStart()            { super.onStart();     mapView.onStart() }
    override fun onPause()            { mapView.onPause();    super.onPause() }
    override fun onStop()             { super.onStop();      mapView.onStop() }
    override fun onDestroy()          { mapView.onDestroy();  super.onDestroy() }
    override fun onLowMemory()        { super.onLowMemory(); mapView.onLowMemory() }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}

// Extension to format your timestamp
fun Long.getDateFormatted(): String {
    val fmt = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
    return fmt.format(java.util.Date(this))
}
