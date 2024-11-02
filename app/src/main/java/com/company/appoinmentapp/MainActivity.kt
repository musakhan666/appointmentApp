package com.company.appoinmentapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.company.appoinmentapp.presentation.appointment.AppointmentViewModel
import com.company.appoinmentapp.ui.theme.AppoinmentAppTheme
import com.company.appoinmentapp.presentation.appointment.AddAppointmentScreen
import com.company.appoinmentapp.presentation.appointment.Constants.DETAIL
import com.company.appoinmentapp.presentation.appointment.Constants.MAIN
import com.company.appoinmentapp.presentation.appointment.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.result.contract.ActivityResultContracts

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Permission launcher for multiple permissions (Calendar and Notification)
    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val calendarGranted = permissions[Manifest.permission.READ_CALENDAR] == true &&
                permissions[Manifest.permission.WRITE_CALENDAR] == true
        val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] == true

        if (calendarGranted) {
            // Calendar permissions granted
            onCalendarPermissionsGranted()
        } else {
            // Handle calendar permissions denied
            onCalendarPermissionsDenied()
        }

        if (notificationGranted) {
            // Notification permission granted
            onNotificationPermissionGranted()
        } else {
            // Handle notification permission denied
            onNotificationPermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check and request permissions
        checkAndRequestPermissions()

        setContent {
            AppoinmentAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: AppointmentViewModel = hiltViewModel()
                    AppointmentApp(viewModel)
                }
            }
        }
    }

    // Check for Calendar and Notification permissions
    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!hasCalendarPermissions()) {
            permissionsToRequest.add(Manifest.permission.READ_CALENDAR)
            permissionsToRequest.add(Manifest.permission.WRITE_CALENDAR)
        }

        if (!hasNotificationPermission()) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun hasCalendarPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasNotificationPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notification permission not required below Android 13
        }
    }

    // Permission result handling for granted/denied states
    private fun onCalendarPermissionsGranted() {
        // Calendar permissions granted; proceed with calendar operations
    }

    private fun onCalendarPermissionsDenied() {
        // Handle the case where calendar permissions are denied (e.g., show a message)
    }

    private fun onNotificationPermissionGranted() {
        // Notification permission granted; proceed with notification operations
    }

    private fun onNotificationPermissionDenied() {
        // Handle the case where notification permission is denied
    }
}

@Composable
fun AppointmentApp(viewModel: AppointmentViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = MAIN) {
        composable(MAIN) {
            MainScreen(viewModel) {
                navController.navigate(DETAIL)
            }
        }
        composable(DETAIL) {
            AddAppointmentScreen(viewModel) {
                navController.popBackStack()
            }
        }
    }
}
