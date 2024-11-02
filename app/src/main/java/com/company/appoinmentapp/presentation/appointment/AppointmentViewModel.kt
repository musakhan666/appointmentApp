package com.company.appoinmentapp.presentation.appointment

import android.content.Context
import android.util.Log
import com.company.appoinmentapp.data.model.Appointment
import com.company.appoinmentapp.utills.addEventToCalendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.company.appoinmentapp.di.dao.AppointmentDao
import com.company.appoinmentapp.utills.cancelReminder
import com.company.appoinmentapp.utills.deleteEventFromCalendar
import com.company.appoinmentapp.utills.scheduleReminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentDao: AppointmentDao,
) : ViewModel() {

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments = _appointments.asStateFlow()

    init {
        viewModelScope.launch {
            appointmentDao.getAppointments().collect {
                _appointments.value = it
            }
        }
    }

    fun addAppointment(appointment: Appointment, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // Add to Room database
            val appointmentId = appointmentDao.insert(appointment)

            // Add to Google Calendar
            val calendarEventId = addEventToCalendar(context, appointment)

            // Update Room with the calendarEventId
            calendarEventId?.let {
                val updatedAppointment =
                    appointment.copy(id = appointmentId.toInt(), calendarEventId = it)
                appointmentDao.update(updatedAppointment)
                scheduleReminder(
                    context,
                    appointmentId.toInt(),
                    it,
                    appointment.dateTime,
                    appointment.title
                )

            }
        }
    }

    fun deleteAppointment(id: Int, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            // Retrieve appointment to get the calendarEventId
            val appointment = _appointments.value.find { it.id == id }
            appointment?.calendarEventId?.let {
                // Delete from Google Calendar
                deleteEventFromCalendar(context, it)
                cancelReminder(context, id)

            }

            Log.i("dataInput", id.toString())


            // Delete from Room
            appointmentDao.deleteById(id)
        }
    }
}
