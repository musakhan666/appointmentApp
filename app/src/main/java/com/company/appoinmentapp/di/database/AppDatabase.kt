package com.company.appoinmentapp.di.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.company.appoinmentapp.data.model.Appointment
import com.company.appoinmentapp.di.dao.AppointmentDao

@Database(entities = [Appointment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appointment_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
