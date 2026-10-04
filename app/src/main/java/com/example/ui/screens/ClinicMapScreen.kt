package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DentalClinic
import com.example.ui.map.RwandaGeographyMap
import com.example.ui.map.RwandaMapHelper
import com.example.viewmodel.AppScreen
import com.example.viewmodel.ClinicFilter
import com.example.viewmodel.DentalViewModel

@Composable
fun ClinicMapScreen(
    viewModel: DentalViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val selectedClinic by viewModel.selectedClinic.collectAsStateWithLifecycle()
    val activeRoute by viewModel.activeRoute.collectAsStateWithLifecycle()
    val isNavigating by viewModel.isNavigating.collectAsStateWithLifecycle()
    val filter by viewModel.clinicFilter.collectAsStateWithLifecycle()

    val clinics = viewModel.filteredClinics
    var showLocationSelector by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("clinic_map_screen")
    ) {
        // 1. Neat Geography Vector Map
        RwandaGeographyMap(
            userLocation = userLocation,
            clinics = clinics,
            selectedClinic = selectedClinic,
            onSelectClinic = { clinic ->
                viewModel.selectClinic(clinic)
            },
            activeRoute = activeRoute,
            isNavigating = isNavigating,
            onStopNavigation = {
                viewModel.stopLiveNavigation()
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Top Filter Chips & GPS Location Sim Switcher Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter)
        ) {
            // Filter Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // GPS Switcher Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.clickable { showLocationSelector = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = userLocation.name.take(16) + if (userLocation.name.length > 16) "..." else "",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0369A1)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showLocationSelector,
                    onDismissRequest = { showLocationSelector = false }
                ) {
                    viewModel.locationPresets.forEach { preset ->
                        DropdownMenuItem(
                            text = { Text(preset.name) },
                            onClick = {
                                viewModel.updateUserLocation(preset.latitude, preset.longitude, preset.name)
                                showLocationSelector = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color(0xFF0284C7))
                            }
                        )
                    }
                }

                FilterChip(
                    selected = filter == ClinicFilter.ALL,
                    onClick = { viewModel.setClinicFilter(ClinicFilter.ALL) },
                    label = { Text("All Clinics (${viewModel.allClinics.size})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0F766E),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = filter == ClinicFilter.MOBILE_VANS,
                    onClick = { viewModel.setClinicFilter(ClinicFilter.MOBILE_VANS) },
                    label = { Text("Mobile Vans 🚐", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFF59E0B),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = filter == ClinicFilter.FIXED_CLINICS,
                    onClick = { viewModel.setClinicFilter(ClinicFilter.FIXED_CLINICS) },
                    label = { Text("Fixed Centers 🏥", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF0F766E),
                        selectedLabelColor = Color.White
                    )
                )

                FilterChip(
                    selected = filter == ClinicFilter.MUTUELLE_ACCEPTED,
                    onClick = { viewModel.setClinicFilter(ClinicFilter.MUTUELLE_ACCEPTED) },
                    label = { Text("Mutuelle CBHI 🛡️", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF16A34A),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // 3. Selected Clinic Floating Bottom Card
        selectedClinic?.let { clinic ->
            val dist = RwandaMapHelper.calculateDistanceKm(
                userLocation.latitude, userLocation.longitude,
                clinic.latitude, clinic.longitude
            )
            val driveMins = ((dist / 35.0f) * 60).toInt().coerceAtLeast(3)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .testTag("selected_clinic_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header row: Clinic Name + Type Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (clinic.isMobileVan) Color(0xFFFEF3C7) else Color(0xFFCCFBF1),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (clinic.isMobileVan) Icons.Default.AirportShuttle else Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = if (clinic.isMobileVan) Color(0xFFD97706) else Color(0xFF0F766E),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = clinic.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                            Text(
                                text = "📍 ${clinic.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Distance & Meta Chips Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE0F2FE)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${String.format(java.util.Locale.US, "%.1f", dist)} km • ~$driveMins mins",
                                    color = Color(0xFF0369A1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${clinic.rating} (${clinic.reviewsCount})",
                                    color = Color(0xFFB45309),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (clinic.acceptsMutuelle) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFDCFCE7)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Mutuelle CBHI",
                                        color = Color(0xFF15803D),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "🕒 ${clinic.openingHours}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF475569)
                    )

                    val doctorNames = clinic.assignedDentists.joinToString(", ") { it.name }
                    if (doctorNames.isNotEmpty()) {
                        Text(
                            text = "👨‍⚕️ Available: $doctorNames",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF0F766E),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons: Real-time Navigation & Book Appointment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (isNavigating) {
                                    viewModel.stopLiveNavigation()
                                } else {
                                    viewModel.startLiveNavigation()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_start_directions"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isNavigating) Color(0xFFDC2626) else Color(0xFF0284C7)
                            )
                        ) {
                            Icon(
                                imageVector = if (isNavigating) Icons.Default.Navigation else Icons.Default.Directions,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isNavigating) "Exit Route" else "Start Directions",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.updateBookingClinic(clinic)
                                viewModel.navigateTo(AppScreen.BOOKING)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_book_here"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Book Visit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
