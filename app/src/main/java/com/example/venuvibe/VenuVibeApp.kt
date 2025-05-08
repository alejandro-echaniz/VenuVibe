package com.example.venuvibe

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.android.libraries.places.api.Places

class VenuVibeApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Offline Persistence for local data
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // Initializing places sdk (once per process)
        if (!Places.isInitialized()) {
            Places.initialize(this, getString(R.string.google_maps_key))
        }
    }
}
