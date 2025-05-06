package com.example.venuvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RatedEventsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // this will inflate rated_events.xml
        setContentView(R.layout.rated_events)
    }
}
