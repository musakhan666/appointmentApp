package com.company.appoinmentapp.utills

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.util.Log
import com.company.appoinmentapp.data.model.Appointment
import java.util.TimeZone
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest

// Function to add an event to Google Calendar and return the event ID
fun addEventToCalendar(context: Context, appointment: Appointment): Long? {
    val values = ContentValues().apply {
        put(CalendarContract.Events.CALENDAR_ID, 1) // Use appropriate calendar ID
        put(CalendarContract.Events.TITLE, appointment.title)
        put(CalendarContract.Events.DESCRIPTION, appointment.description)
        put(CalendarContract.Events.EVENT_LOCATION, appointment.location)
        put(CalendarContract.Events.DTSTART, appointment.dateTime)
        put(CalendarContract.Events.DTEND, appointment.dateTime + 60 * 60 * 1000) // 1-hour duration
        put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
    }
    val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    return uri?.lastPathSegment?.toLongOrNull()
}

// Function to delete an event from Google Calendar by event ID
fun deleteEventFromCalendar(context: Context, eventId: Long) {
    val deleteUri =
        CalendarContract.Events.CONTENT_URI.buildUpon().appendPath(eventId.toString()).build()
    context.contentResolver.delete(deleteUri, null, null)
}


fun scheduleReminder(
    context: Context,
    appointmentId: Int,
    calendarId: Long,
    appointmentTime: Long,
    appointmentName: String
) {
    val delay = appointmentTime - System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24)

    if (delay > 0) {
        val data = Data.Builder()
            .putString("title", appointmentName)
            .putString("message", "You have an appointment scheduled in 24 hours.")
            .putInt("notification_id", appointmentId)
            .putLong("calendar_id", calendarId)
            .build()

        // Change WorkRequest to OneTimeWorkRequest here
        val reminderRequest: OneTimeWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()


        // Use OneTimeWorkRequest as the third parameter
        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_$appointmentId",
            ExistingWorkPolicy.REPLACE,
            reminderRequest
        )
    }
}

fun cancelReminder(context: Context, appointmentId: Int) {
    WorkManager.getInstance(context).cancelUniqueWork("reminder_$appointmentId")
}
