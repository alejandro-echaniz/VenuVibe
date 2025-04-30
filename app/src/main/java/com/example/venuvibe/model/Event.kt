package com.example.venuvibe.model

// This class is for representing one event
data class Event(
    var id: String        = "",
    var title: String     = "",
    var description: String = "",
    var latitude: Double  = 0.0,
    var longitude: Double = 0.0,
    var date: Long        = 0L,
    var averageRating: Float = 0f
)