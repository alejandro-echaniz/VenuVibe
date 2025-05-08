package com.example.venuvibe.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Event(
    var id: String         = "",
    var title: String      = "",
    var description: String = "",
    var latitude: Double   = 0.0,
    var longitude: Double  = 0.0,
    var date: Long         = 0L,
    var averageRating: Float = 0f
) : Parcelable {

    fun getDateFormatted(): String {
        val formatter = java.text.SimpleDateFormat(
            "MM/dd/yy, h:mm a",
            java.util.Locale.getDefault()
        )
        return formatter.format(java.util.Date(date))
    }
}
