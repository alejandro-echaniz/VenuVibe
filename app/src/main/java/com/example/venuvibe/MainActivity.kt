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


        /* THIS IS FOR TESTING IF FIREBASE CONNECTION WORKED, NOT FOR ACTUAL PROJECT CODE

        val healthRef = firebase.getReference("health_check")

        healthRef.setValue("OK at ${System.currentTimeMillis()}") { error, _ ->
            if (error != null) {
                // DatabaseError → Throwable
                Log.e("MainActivity", "Health check write failed", error.toException())
            } else {
                Log.d("MainActivity", "Health check write succeeded")
            }
        }

        healthRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java)
                Log.d("MainActivity", "Health check read: $value")
            }
            override fun onCancelled(dbError: DatabaseError) {
                // DatabaseError → Throwable
                Log.e("MainActivity", "Health check read cancelled", dbError.toException())
            }
        })
        */


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
        // Load events
    }
}