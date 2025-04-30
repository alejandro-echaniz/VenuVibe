package com.example.venuvibe.model

// This class is for a user review of an event
data class Review(
    var userId: String    = "",
    var rating: Float     = 0f,
    var comment: String   = "",
    var timestamp: Long   = 0L
)