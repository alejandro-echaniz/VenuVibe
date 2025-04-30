package com.example.venuvibe.data

import com.example.venuvibe.model.Event
import com.example.venuvibe.model.Review
import com.google.firebase.database.*

// All realtime database read/writes for Events and Reviews
class EventRepository(
    private val firebase: FirebaseDatabase,
    private val eventsRef: DatabaseReference
) {
    private val reviewsRef: DatabaseReference = firebase.getReference("reviews")

    // Real time listener on events
    fun getAllEvents(onUpdate: (List<Event>) -> Unit, onError:   (DatabaseError) -> Unit) {
        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Event>()
                for (child in snapshot.children) {
                    child.getValue(Event::class.java)?.apply {
                        id = child.key ?: ""
                        list.add(this)
                    }
                }
                onUpdate(list)
            }
            override fun onCancelled(error: DatabaseError) = onError(error)
        })
    }

    // Gets events for a specific day
    fun getEventsForDate(targetDayMs : Long, onUpdate : (List<Event>) -> Unit, onError : (DatabaseError) -> Unit) {
        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Event>()
                for (child in snapshot.children) {
                    child.getValue(Event::class.java)?.apply {
                        id = child.key ?: ""
                        // only include events on that exact day
                        if (date == targetDayMs) {
                            list.add(this)
                        }
                    }
                }
                onUpdate(list)
            }
            override fun onCancelled(error: DatabaseError) = onError(error)
        })
    }

    // Write a new event
    fun addEvent(event: Event, onComplete: (DatabaseError?) -> Unit) {
        val newRef = eventsRef.push()
        event.id = newRef.key ?: ""
        newRef.setValue(event) { err, _ -> onComplete(err) }
    }

    // Real time listener on reviews
    fun getReviews(eventId: String, onUpdate: (List<Review>) -> Unit, onError:   (DatabaseError) -> Unit) {
        reviewsRef.child(eventId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Review>()
                    for (child in snapshot.children) {
                        child.getValue(Review::class.java)?.let { list.add(it) }
                    }
                    onUpdate(list)
                }
                override fun onCancelled(error: DatabaseError) = onError(error)
            })
    }

    // Write a new review
    fun addReview(eventId: String, review: Review, onComplete: (DatabaseError?) -> Unit) {
        val newRef = reviewsRef.child(eventId).push()
        newRef.setValue(review) { err, _ -> onComplete(err) }
    }
}