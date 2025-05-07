package com.example.venuvibe

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.venuvibe.data.EventRepository
import com.example.venuvibe.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.text.SimpleDateFormat
import java.util.*

/* Advertisement imports */
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.AdView

/* Hooking up FABS */
import com.google.android.material.floatingactionbutton.FloatingActionButton

/* User Preferences */
import com.example.venuvibe.data.UserPrefs

class MainActivity : AppCompatActivity() {
    // Firebase remote db setup
    private lateinit var firebase: FirebaseDatabase
    private lateinit var eventsRef: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var repository: EventRepository

    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarView: CalendarView
    private lateinit var eventAdapter: EventAdapter

    private var allEvents: List<Event> = listOf()
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply saved theme before inflating layout
        val isDark = UserPrefs.isDarkModeEnabled(this)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 1) Wire up toolbar and theme toggle
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)

        val accent = UserPrefs.getAccentColor(this)
        toolbar.setBackgroundColor(accent)

        setSupportActionBar(toolbar)

        val toggle = toolbar.findViewById<TextView>(R.id.toggleTheme)
        // initialize icon
        toggle.text = if (isDark) "☀️" else "🌙"
        toggle.setOnClickListener {
            // flip the saved preference
            val nextDark = !UserPrefs.isDarkModeEnabled(this)
            UserPrefs.saveDarkModeEnabled(this, nextDark)

            // apply the new mode (will recreate activity)
            AppCompatDelegate.setDefaultNightMode(
            if (nextDark) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        firebase = FirebaseDatabase.getInstance()
        eventsRef = firebase.getReference("events")
        auth = FirebaseAuth.getInstance()
        repository = EventRepository(firebase, eventsRef)

        recyclerView = findViewById(R.id.rvEvents)
        calendarView = findViewById(R.id.calendarView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        eventAdapter = EventAdapter(listOf()) { event ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("event", event)
            startActivity(intent)
        }
        recyclerView.adapter = eventAdapter

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAddEvent).setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabRatedEvents).setOnClickListener {
            val intent = Intent(this, RatedEventsActivity::class.java)
            startActivity(intent)
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth, 0, 0)
            selectedDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
            filterEventsByDate()
        }

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
        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventList = mutableListOf<Event>()
                for (child in snapshot.children) {
                    val event = child.getValue(Event::class.java)
                    if (event != null) {
                        eventList.add(event)
                    }
                }
                allEvents = eventList
                filterEventsByDate() // if a date is already selected
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to load events: ${error.message}")
            }
        })
    }

    private fun filterEventsByDate() {
        if (selectedDate.isEmpty()) {
            eventAdapter.updateEvents(allEvents)
            return
        }

        val filtered = allEvents.filter { event ->
            val dateFormatted = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(event.date))
            dateFormatted == selectedDate
        }
        eventAdapter.updateEvents(filtered)
    }

    // 2) Inflate your menu_main.xml
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        // restore “Show past” checkbox from prefs
        // menu.findItem(R.id.action_show_past).isChecked = UserPrefs.isShowPastEnabled(this)
        return true
    }

    // 3) Handle both menu actions: “Show past” and “Accent”
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /* R.id.action_show_past -> {
                // toggle & persist
                val next = !item.isChecked
                item.isChecked = next
                UserPrefs.saveShowPastEnabled(this, next)
                filterEventsByDate()
                true
            } */

            R.id.action_pick_color -> {
                // show a simple color picker dialog
                val colors = arrayOf("Purple", "Green", "Teal")
                val colorValues = arrayOf(
                    0xFF6200EE.toInt(),
                    0xFF388E3C.toInt(),
                    0xFF00796B.toInt()
                    )
                AlertDialog.Builder(this)
                    .setTitle("Pick an accent")
                    .setItems(colors) { _, which ->
                        val chosen = colorValues[which]
                        // persist
                        UserPrefs.saveAccentColor(this, chosen)
                        // immediately re-tint toolbar
                        findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
                            .setBackgroundColor(chosen)
                    }
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}