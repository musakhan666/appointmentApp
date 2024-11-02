package com.company.appoinmentapp.utills

import android.Manifest
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.company.appoinmentapp.R
import com.company.appoinmentapp.di.dao.AppointmentDao
import com.company.appoinmentapp.di.database.AppDatabase
import kotlinx.coroutines.runBlocking

class ReminderWorker(
    val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    private val appointmentDao: AppointmentDao =
        AppDatabase.getDatabase(context).appointmentDao() // Get DAO from AppDatabase

    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Appointment Reminder"
        val message = inputData.getString("message") ?: "You have an appointment in 24 hours."
        val notificationId = inputData.getInt("notification_id", 0)

        val calendarId = inputData.getLong("calendar_id", 0)

        showNotification(applicationContext, title, message, notificationId)
        // Remove appointment from records if ID is valid

        Log.i("dataInput", inputData.toString())

        if (notificationId != 0) {
            runBlocking {
                appointmentDao.deleteById(notificationId) // Delete the appointment by ID
                deleteEventFromCalendar(context = context, calendarId)
            }
        }

        return Result.success()
    }
}


fun showNotification(context: Context, title: String, message: String, notificationId: Int) {
    val channelId = "appointment_reminder_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Appointment Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for appointment reminders"
        }
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.mipmap.ic_launcher) // Use an appropriate icon
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }
    NotificationManagerCompat.from(context).notify(notificationId, builder.build())
}
