package com.example.venuvibe

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class VenuVibeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true) // runs only ONCE before firebase
    }
}
