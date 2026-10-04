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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.SampleData
import com.example.viewmodel.DentalViewModel

@Composable
fun MedicalHistoryScreen(
    viewModel: DentalViewModel,
    modifier: Modifier = Modifier
) {
    val patientRecord by viewModel.patientRecord.collectAsStateWithLifecycle()
    val record = patientRecord ?: SampleData.sampleInitialMedicalRecord

    var showEditDialog by remember { mutableStateOf(false) }
    var editAllergies by remember { mutableStateOf(record.allergies) }
    var editConditions by remember { mutableStateOf(record.chronicConditions) }
    var editNotes by remember { mutableStateOf(record.dentalNotes) }

    var selectedToothNumber by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("medical_history_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Patient Header Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0F766E))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                        Button(
                            onClick = {
                                editAllergies = record.allergies
                                editConditions = record.chronicConditions
                                editNotes = record.dentalNotes
                                showEditDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit Record", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = record.patientName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "National ID: ${record.patientNationalId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFCCFBF1)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bloodtype, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Blood: ${record.bloodType}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RSSB / Mutuelle Active", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Interactive Dental Odontogram (Tooth Chart)
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Interactive Odontogram Chart",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "FDI / Universal adult dental map",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Upper Arch
                    Text("Upper Dental Arch (Maxillary)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F766E))
                    Spacer(modifier = Modifier.height(6.dp))
                    val upperTeeth = listOf("18", "17", "16", "15", "14", "13", "12", "11", "21", "22", "23", "24", "25", "26", "27", "28")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        upperTeeth.forEach { tooth ->
                            val status = if (tooth == "16") "filled" else "healthy"
                            ToothItem(
                                number = tooth,
                                status = status,
                                isSelected = selectedToothNumber == tooth,
                                onClick = { selectedToothNumber = tooth }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lower Arch
                    Text("Lower Dental Arch (Mandibular)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F766E))
                    Spacer(modifier = Modifier.height(6.dp))
                    val lowerTeeth = listOf("48", "47", "46", "45", "44", "43", "42", "41", "31", "32", "33", "34", "35", "36", "37", "38")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        lowerTeeth.forEach { tooth ->
                            val status = if (tooth == "36") "cleaned" else "healthy"
                            ToothItem(
                                number = tooth,
                                status = status,
                                isSelected = selectedToothNumber == tooth,
                                onClick = { selectedToothNumber = tooth }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tooth Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        LegendChip(color = Color(0xFF10B981), text = "Healthy")
                        LegendChip(color = Color(0xFF0284C7), text = "Composite Filled")
                        LegendChip(color = Color(0xFFF59E0B), text = "Scaled / Cleaned")
                    }

                    selectedToothNumber?.let { num ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Tooth #$num: ${if (num == "16") "Restored with Atraumatic composite filling in 2024. Stable margin." else if (num == "36") "Deep ultrasonic scaling performed. Clean." else "Healthy enamel. No visible caries."}",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp),
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }

        // Allergies & Medical Alerts Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Medical Alerts & Allergies",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Allergies:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    Text(record.allergies, fontSize = 13.sp, color = Color(0xFF1E293B))

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Chronic Conditions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    Text(record.chronicConditions, fontSize = 13.sp, color = Color(0xFF1E293B))

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Bleeding Disorder Risk:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    Text(if (record.bleedingDisorders) "Yes - Requires careful local hemostatic care" else "No abnormal bleeding history", fontSize = 13.sp, color = Color(0xFF1E293B))

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Emergency Contact:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                    Text("${record.emergencyContact} (${record.emergencyPhone})", fontSize = 13.sp, color = Color(0xFF0F766E))
                }
            }
        }

        // Past Consultations & Clinical Notes
        item {
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clinical Consultation Notes & History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = record.dentalNotes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334155),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Last Dental Cleaning: ${record.lastCleaningDate}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // Edit Medical Record Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateMedicalRecord(
                            allergies = editAllergies,
                            chronicConditions = editConditions,
                            dentalNotes = editNotes
                        )
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                ) {
                    Text("Save to Room DB")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Update Patient Medical History") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editAllergies,
                        onValueChange = { editAllergies = it },
                        label = { Text("Allergies (Penicillin, Latex, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editConditions,
                        onValueChange = { editConditions = it },
                        label = { Text("Chronic Conditions (Hypertension, etc.)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editNotes,
                        onValueChange = { editNotes = it },
                        label = { Text("Dental History Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun ToothItem(
    number: String,
    status: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when (status) {
        "filled" -> Color(0xFF0284C7)
        "cleaned" -> Color(0xFFF59E0B)
        else -> Color(0xFF10B981)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) Color(0xFF0F766E) else bgColor.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF0F766E) else bgColor
        ),
        modifier = Modifier
            .size(38.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF1E293B)
            )
        }
    }
}

@Composable
private fun LegendChip(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 10.sp, color = Color(0xFF64748B))
    }
}
