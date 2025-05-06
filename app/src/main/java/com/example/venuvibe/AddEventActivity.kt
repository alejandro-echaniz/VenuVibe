package com.example.venuvibe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AddEventActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // this will inflate item_event.xml
        setContentView(R.layout.item_event)
    }
}
