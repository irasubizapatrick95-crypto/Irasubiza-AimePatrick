package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Appointment
import com.example.data.model.DentalClinic
import com.example.data.model.DentalService
import com.example.data.model.Dentist
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DentalViewModel

@Composable
fun BookingScreen(
    viewModel: DentalViewModel,
    modifier: Modifier = Modifier
) {
    val bookingClinic by viewModel.bookingClinic.collectAsStateWithLifecycle()
    val bookingDentist by viewModel.bookingDentist.collectAsStateWithLifecycle()
    val bookingService by viewModel.bookingService.collectAsStateWithLifecycle()
    val bookingDate by viewModel.bookingDate.collectAsStateWithLifecycle()
    val bookingTimeSlot by viewModel.bookingTimeSlot.collectAsStateWithLifecycle()
    val isEmergency by viewModel.isEmergency.collectAsStateWithLifecycle()
    val patientName by viewModel.patientName.collectAsStateWithLifecycle()
    val patientPhone by viewModel.patientPhone.collectAsStateWithLifecycle()
    val nationalId by viewModel.nationalId.collectAsStateWithLifecycle()
    val symptomsNotes by viewModel.symptomsNotes.collectAsStateWithLifecycle()
    val paymentMethod by viewModel.paymentMethod.collectAsStateWithLifecycle()
    val isProcessingPayment by viewModel.isProcessingPayment.collectAsStateWithLifecycle()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var confirmedAppointment by remember { mutableStateOf<Appointment?>(null) }

    // Dropdown expansion states
    var showClinicDropdown by remember { mutableStateOf(false) }
    var showDentistDropdown by remember { mutableStateOf(false) }
    var showServiceDropdown by remember { mutableStateOf(false) }

    val dateOptions = listOf("Today, Urgent", "Oct 12, 2026", "Oct 13, 2026", "Oct 14, 2026", "Oct 16, 2026")
    val timeSlots = listOf("08:30 AM", "10:00 AM", "11:30 AM", "02:00 PM", "03:30 PM", "05:00 PM")

    val servicePrice = bookingService?.priceRwf ?: 8000
    val insuranceCoverPct = if (paymentMethod == "MUTUELLE_CBHI" || paymentMethod == "RAMA") {
        bookingService?.insuranceCoveredPercentage ?: 90
    } else 0
    val coveredAmount = (servicePrice * insuranceCoverPct) / 100
    val patientCoPay = servicePrice - coveredAmount

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("booking_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title header
        item {
            Column {
                Text(
                    text = "Request Dental Appointment",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "Rwanda Clinic & Mobile Van Network • Kalahari Dental Care",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        // Emergency Toggle Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isEmergency) Color(0xFFFEF2F2) else Color(0xFFF8FAFC)
                ),
                border = if (isEmergency) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDC2626)) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = null,
                        tint = if (isEmergency) Color(0xFFDC2626) else Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Emergency Severe Toothache Triage",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isEmergency) Color(0xFF991B1B) else Color(0xFF1E293B)
                        )
                        Text(
                            text = "Prioritizes immediate doctor dispatch or nearest mobile dental unit.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isEmergency,
                        onCheckedChange = { viewModel.updateIsEmergency(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFDC2626)
                        ),
                        modifier = Modifier.testTag("switch_emergency")
                    )
                }
            }
        }

        // Step 1: Select Clinic or Mobile Van
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "1. Select Facility or Mobile Unit",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showClinicDropdown = true }
                                .padding(vertical = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (bookingClinic?.isMobileVan == true) Icons.Default.AirportShuttle else Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = if (bookingClinic?.isMobileVan == true) Color(0xFFF59E0B) else Color(0xFF0F766E)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bookingClinic?.name ?: "Select Clinic",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${bookingClinic?.district}, ${bookingClinic?.province}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Text("Change", color = Color(0xFF0F766E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(
                            expanded = showClinicDropdown,
                            onDismissRequest = { showClinicDropdown = false }
                        ) {
                            viewModel.allClinics.forEach { clinic ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = if (clinic.isMobileVan) "🚐 ${clinic.name}" else "🏥 ${clinic.name}",
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${clinic.district} • ${clinic.address}",
                                                fontSize = 11.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.updateBookingClinic(clinic)
                                        showClinicDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 2: Select Dentist
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "2. Select Dental Surgeon",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDentistDropdown = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF0F766E).copy(alpha = 0.15f),
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF0F766E))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bookingDentist?.name ?: "Select Dentist",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${bookingDentist?.specialty} • ${bookingDentist?.experienceYears} yrs exp",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Text("Change", color = Color(0xFF0F766E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(
                            expanded = showDentistDropdown,
                            onDismissRequest = { showDentistDropdown = false }
                        ) {
                            val availableDentists = bookingClinic?.assignedDentists?.ifEmpty { viewModel.allDentists } ?: viewModel.allDentists
                            availableDentists.forEach { dentist ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(dentist.name, fontWeight = FontWeight.Bold)
                                            Text("${dentist.specialty} • ★ ${dentist.rating}", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                    },
                                    onClick = {
                                        viewModel.updateBookingDentist(dentist)
                                        showDentistDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 3: Select Dental Procedure
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "3. Select Dental Procedure",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showServiceDropdown = true }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bookingService?.title ?: "Select Service",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Duration: ~${bookingService?.durationMinutes} mins • Price: ${bookingService?.priceRwf} RWF",
                                        fontSize = 12.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Text("Change", color = Color(0xFF0F766E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(
                            expanded = showServiceDropdown,
                            onDismissRequest = { showServiceDropdown = false }
                        ) {
                            viewModel.allServices.forEach { service ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(service.title, fontWeight = FontWeight.Bold)
                                            Text(
                                                "${service.priceRwf} RWF • Mutuelle covers ${service.insuranceCoveredPercentage}%",
                                                fontSize = 11.sp,
                                                color = Color(0xFF0F766E)
                                            )
                                        }
                                    },
                                    onClick = {
                                        viewModel.updateBookingService(service)
                                        showServiceDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 4: Pick Date & Time Slot
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "4. Appointment Date & Slot",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Preferred Date:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dateOptions.forEach { date ->
                            val isSelected = bookingDate == date
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF0F766E) else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { viewModel.updateBookingDate(date) }
                            ) {
                                Text(
                                    text = date,
                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Available Time Slots:", style = MaterialTheme.typography.labelMedium, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timeSlots.forEach { slot ->
                            val isSelected = bookingTimeSlot == slot
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { viewModel.updateBookingTimeSlot(slot) }
                            ) {
                                Text(
                                    text = slot,
                                    color = if (isSelected) Color.White else Color(0xFF334155),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 5: Patient Details & National ID
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "5. Patient Identification & Health ID",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )

                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { viewModel.updatePatientName(it) },
                        label = { Text("Full Patient Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("input_patient_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = patientPhone,
                        onValueChange = { viewModel.updatePatientPhone(it) },
                        label = { Text("Rwandan Mobile Phone (MoMo / Airtel)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("input_patient_phone"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = nationalId,
                        onValueChange = { viewModel.updateNationalId(it) },
                        label = { Text("Rwandan National ID / Indangamuntu (16 digits)") },
                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_national_id"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = symptomsNotes,
                        onValueChange = { viewModel.updateSymptomsNotes(it) },
                        label = { Text("Chief Complaint / Symptoms Description") },
                        placeholder = { Text("e.g. Tooth sensitivity, lower molar cavity, bleeding gums") },
                        modifier = Modifier.fillMaxWidth().testTag("input_symptoms"),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )
                }
            }
        }

        // Step 6: Secure Digital Payment Method (Rwanda)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "6. Secure Digital Payment Method",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val paymentMethods = listOf(
                        Triple("MTN_MOMO", "MTN Mobile Money (*182#)", "Instant USSD Push prompt"),
                        Triple("MUTUELLE_CBHI", "Mutuelle de Santé (RSSB)", "90% Covered with National ID"),
                        Triple("AIRTEL_MONEY", "Airtel Money Rwanda", "Digital wallet verification"),
                        Triple("RAMA", "RAMA / RSSB Insured", "Public servant healthcare scheme"),
                        Triple("CASH", "Pay at Clinic Desk", "Cash or MoMo on arrival")
                    )

                    paymentMethods.forEach { (id, name, desc) ->
                        val isSelected = paymentMethod == id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFFCCFBF1) else Color(0xFFF8FAFC),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F766E)) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.updatePaymentMethod(id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSelected) Color(0xFF0F766E) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    if (isSelected) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color(0xFF0F766E) else Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-time Rwandan Francs Price Computation Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF0FDF4),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Standard Procedure Fee:", fontSize = 12.sp, color = Color(0xFF475569))
                                Text("$servicePrice RWF", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            if (insuranceCoverPct > 0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Insurance Coverage ($insuranceCoverPct%):", fontSize = 12.sp, color = Color(0xFF16A34A))
                                    Text("-$coveredAmount RWF", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Patient Payable Co-Pay:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "$patientCoPay RWF",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF0F766E)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Confirm & Pay CTA
        item {
            Button(
                onClick = {
                    viewModel.bookAppointment { appt ->
                        confirmedAppointment = appt
                        showSuccessDialog = true
                    }
                },
                enabled = !isProcessingPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_confirm_booking"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
            ) {
                if (isProcessingPayment) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Processing MoMo / Health Insurance...")
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm & Request Appointment ($patientCoPay RWF)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Success E-Receipt Dialog
    if (showSuccessDialog && confirmedAppointment != null) {
        val appt = confirmedAppointment!!
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.navigateTo(AppScreen.APPOINTMENTS)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                ) {
                    Text("View in Visits")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showSuccessDialog = false
                        viewModel.navigateTo(AppScreen.MAP_LOCATOR)
                    }
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View on Map")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Appointment Confirmed!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Your dental visit has been scheduled and recorded in the clinic database.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🇷🇼 Kalahari Dental Clinic Digital E-Ticket", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF0F766E))
                            Text("Booking Ref: #${appt.paymentReference}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Facility: ${appt.clinicName}", fontSize = 12.sp)
                            Text("Dentist: ${appt.dentistName}", fontSize = 12.sp)
                            Text("Date: ${appt.date} at ${appt.timeSlot}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Service: ${appt.serviceTitle}", fontSize = 12.sp)
                            Text("Payment: ${appt.paymentMethod} (${appt.paymentStatus})", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
