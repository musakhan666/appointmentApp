package com.company.appoinmentapp.presentation.appointment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.company.appoinmentapp.R
import com.company.appoinmentapp.data.model.Appointment
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MainScreen(
    viewModel: AppointmentViewModel,
    navigate: (String) -> Unit
) {
    val appointments by viewModel.appointments.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedButton(
            onClick = { navigate(Constants.DETAIL) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_appointment),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.add_appointment), color = Color.White)
        }

        Text(
            text = stringResource(R.string.your_appointments),
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF333333),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(appointments.size) { index ->
                val appointment = appointments[index]
                AppointmentItem(
                    appointment = appointment,
                    onDelete = {
                        viewModel.deleteAppointment(appointment.id, context = context)
                        Toast.makeText(context, "Appointment deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}

@Composable
fun AppointmentItem(
    appointment: Appointment,
    onDelete: () -> Unit
) {
    val formattedDateTime = formatDateTime(appointment.dateTime)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = appointment.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF333333),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = appointment.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formattedDateTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_appointment),
                    tint = Color(0xFFD32F2F)
                )
            }
        }
    }
}

@Composable
fun AddAppointmentScreen(
    viewModel: AppointmentViewModel,
    onAdd: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("Select Date") }
    var time by remember { mutableStateOf("Select Time") }
    var location by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.add_new_appointment),
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 8.dp, top = 30.dp)
        )

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Date Selector styled as TextField
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .clickable {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                .padding(16.dp)
        ) {
            Text(
                text = date,
                color = if (date == "Select Date") Color.Gray else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Time Selector styled as TextField
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(4.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                .clickable {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            time = String.format("%02d:%02d", hour, minute)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }
                .padding(16.dp)
        ) {
            Text(
                text = time,
                color = if (time == "Select Time") Color.Gray else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        TextField(
            value = location,
            onValueChange = { location = it },
            label = { Text(stringResource(R.string.location)) },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val dateTime = parseDateTime(date, time)
                if (title.isNotBlank() && dateTime != null && location.isNotBlank()) {
                    val newAppointment = Appointment(
                        title = title,
                        description = description,
                        dateTime = dateTime,
                        location = location
                    )
                    viewModel.addAppointment(newAppointment, context)
                    Toast.makeText(context, "Appointment saved successfully", Toast.LENGTH_SHORT).show()
                    onAdd()
                } else {
                    Toast.makeText(context, R.string.error_fill_required_fields, Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.save_appointment), color = Color.White)
        }
    }
}







// Helper Functions
fun formatDateTime(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMM yyyy 'at' HH:mm", Locale.getDefault())
    return format.format(date)
}

fun parseDateTime(date: String, time: String): Long? {
    return try {
        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateTime = "$date $time"
        dateTimeFormat.parse(dateTime)?.time
    } catch (e: Exception) {
        null
    }
}

object Constants {
    const val DETAIL = "DETAIL"
    const val MAIN = "MAIN"
}
