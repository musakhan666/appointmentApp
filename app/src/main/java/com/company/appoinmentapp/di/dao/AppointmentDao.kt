package com.company.appoinmentapp.di.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.company.appoinmentapp.data.model.Appointment
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY dateTime DESC")
    fun getAppointments(): Flow<List<Appointment>>

    @Insert
    suspend fun insert(appointment: Appointment): Long

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: Int): Int  // Updated return type to `Int`

    // Update method to allow updating the calendarEventId if needed
    @Update
    suspend fun update(appointment: Appointment)
}
