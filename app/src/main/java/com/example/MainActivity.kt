package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DentalBottomBar
import com.example.ui.components.DentalTopAppBar
import com.example.ui.screens.AIScreeningScreen
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.BookingScreen
import com.example.ui.screens.CPDAndClinicianScreen
import com.example.ui.screens.ClinicMapScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicalHistoryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DentalViewModel
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private val viewModel: DentalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                DentalApp(viewModel)
            }
        }
    }
}

@Composable
fun DentalApp(viewModel: DentalViewModel) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()

    // Location Permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.updateUserLocation(loc.latitude, loc.longitude, "My Current GPS Location")
                    }
                }
            } catch (_: SecurityException) {}
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFine) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.updateUserLocation(loc.latitude, loc.longitude, "My Current GPS Location")
                    }
                }
            } catch (_: SecurityException) {}
        }
    }

    // Handle Back Press gracefully when navigating secondary screens
    if (currentScreen != AppScreen.HOME) {
        BackHandler {
            viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            DentalTopAppBar(
                currentScreen = currentScreen,
                currentLanguage = language,
                onSelectLanguage = { viewModel.setLanguage(it) }
            )
        },
        bottomBar = {
            DentalBottomBar(
                currentScreen = currentScreen,
                onNavigate = { viewModel.navigateTo(it) }
            )
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "screen_crossfade"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                AppScreen.MAP_LOCATOR -> ClinicMapScreen(viewModel = viewModel)
                AppScreen.BOOKING -> BookingScreen(viewModel = viewModel)
                AppScreen.APPOINTMENTS -> AppointmentsScreen(viewModel = viewModel)
                AppScreen.AI_SCREENING -> AIScreeningScreen(viewModel = viewModel)
                AppScreen.MEDICAL_HISTORY -> MedicalHistoryScreen(viewModel = viewModel)
                AppScreen.CLINICIAN_WORKSPACE -> CPDAndClinicianScreen(viewModel = viewModel)
            }
        }
    }
}
