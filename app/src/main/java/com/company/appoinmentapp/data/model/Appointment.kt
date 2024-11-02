package com.company.appoinmentapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dateTime: Long, // Store the date and time in milliseconds
    val location: String,
    val calendarEventId: Long? = null // Nullable, because it might not always be associated with a Google Calendar event
)

