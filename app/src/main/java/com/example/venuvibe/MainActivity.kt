package com.example.venuvibe

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import com.example.venuvibe.data.EventRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

/* Advertisement imports */
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdView

/* Hooking up FABS */
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    // Firebase remote db setup
    private lateinit var firebase: FirebaseDatabase
    private lateinit var eventsRef: DatabaseReference

    // Firebase Auth for logins/user ids
    private lateinit var auth: FirebaseAuth

    // data layer
    private lateinit var repository: EventRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init remote db
        firebase = FirebaseDatabase.getInstance()
        firebase.setPersistenceEnabled(true)
        eventsRef = firebase.getReference("events")

        // init auth
        auth = FirebaseAuth.getInstance()

        // init data layer
        repository = EventRepository(firebase, eventsRef)


        // anonymous login
        if (auth.currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startApp()
                } else {
                    Toast.makeText(
                        this,
                        "Auth failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else {
            startApp()
        }
    }

    // Called after user signs in and db ready
    private fun startApp() {
        // Can't really code this until the other files are coded
        // LOADING ADVERTISEMENT
        MobileAds.initialize(this) {}

        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // LOADING EVENTS

        // ADD EVENT
        findViewById<FloatingActionButton>(R.id.fabAddEvent)
            .setOnClickListener {
                startActivity(Intent(this, AddEventActivity::class.java))
            }

        //
        findViewById<FloatingActionButton>(R.id.fabRatedEvents)
            .setOnClickListener {
                startActivity(Intent(this, RatedEventsActivity::class.java))
            }
    }
}