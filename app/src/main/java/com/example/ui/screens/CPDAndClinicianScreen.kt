package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.CPDModule
import com.example.viewmodel.DentalViewModel

@Composable
fun CPDAndClinicianScreen(
    viewModel: DentalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val screenings by viewModel.screenings.collectAsStateWithLifecycle()
    val cpdModules = viewModel.cpdModules
    val campaigns = viewModel.outreachCampaigns

    var activeQuizModule by remember { mutableStateOf<CPDModule?>(null) }
    var quizAnswerSelected by remember { mutableIntStateOf(-1) }
    var quizSubmitted by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("cpd_clinician_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Color(0xFF0F766E)
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Clinician CPD Hub", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Triage Inbox (${screenings.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("Outreach", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedSection) {
                0 -> {
                    // Section 0: CPD Learning Modules
                    item {
                        Column {
                            Text(
                                text = "Continuing Professional Development (CPD)",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Rwanda Dental Association accredited microcourses for dentists, mobile operators, and oral health officers.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    // Clinician CPD stats card
                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFF0F766E))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Dr. Aimé Patrick Irasubiza", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                                    Text("License: RMD/DEN-2018-092", color = Color(0xFFCCFBF1), fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("2026 Annual Points: 21 / 30 CPD Units", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                }
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF14B8A6),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    items(cpdModules) { module ->
                        ElevatedCard(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFE0F2FE)
                                    ) {
                                        Text(
                                            text = module.category,
                                            color = Color(0xFF0284C7),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = "+${module.points} CPD Credits",
                                            color = Color(0xFFB45309),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = module.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = module.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF475569)
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "⏱️ ${module.durationMinutes} mins • ${module.lessons} lessons",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )

                                    Button(
                                        onClick = {
                                            activeQuizModule = module
                                            quizAnswerSelected = -1
                                            quizSubmitted = false
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (module.isCompleted) Color(0xFF16A34A) else Color(0xFF0F766E)
                                        ),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            if (module.isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (module.isCompleted) "Completed" else "Start Quiz", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Section 1: Referral & Shadow-Mode Triage Inbox
                    item {
                        Column {
                            Text(
                                text = "AI Shadow-Mode Referral Inbox",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Dentist oversight panel for automated community questionnaires and risk triage.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    if (screenings.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No pending AI screenings right now.", color = Color(0xFF64748B))
                                }
                            }
                        }
                    } else {
                        items(screenings) { screening ->
                            ElevatedCard(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when (screening.riskLevel) {
                                                "URGENT" -> Color(0xFFDC2626)
                                                "HIGH" -> Color(0xFFF59E0B)
                                                else -> Color(0xFF16A34A)
                                            }
                                        ) {
                                            Text(
                                                text = screening.riskLevel,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }

                                        Text(
                                            text = screening.date,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Finding: ${screening.probableCondition}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Complaint: \"${screening.chiefComplaint}\"",
                                        fontSize = 12.sp,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = "Pain: ${screening.painScore}/10 • Duration: ${screening.durationDays} days",
                                        fontSize = 11.sp,
                                        color = Color(0xFF0F766E),
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        if (screening.triageVerifiedByDentist) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Verified by Dentist", color = Color(0xFF16A34A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Button(
                                                onClick = { viewModel.verifyScreening(screening.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text("Approve & Accept Referral", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Section 2: Outreach Dashboard
                    item {
                        Column {
                            Text(
                                text = "Community Outreach Campaigns",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Mobile dental van school visits, workplace screenings, and vulnerable community reach.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    items(campaigns) { campaign ->
                        ElevatedCard(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (campaign.status == "Active Today") Color(0xFFDCFCE7) else Color(0xFFE0F2FE)
                                    ) {
                                        Text(
                                            text = campaign.status,
                                            color = if (campaign.status == "Active Today") Color(0xFF15803D) else Color(0xFF0369A1),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(campaign.date, fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = campaign.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "📍 ${campaign.location} (${campaign.district})",
                                    fontSize = 12.sp,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = "🎯 Target: ${campaign.targetGroup} • Expected: ~${campaign.expectedBeneficiaries} beneficiaries",
                                    fontSize = 11.sp,
                                    color = Color(0xFF0F766E),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // CPD Quiz Dialog
    activeQuizModule?.let { mod ->
        AlertDialog(
            onDismissRequest = { activeQuizModule = null },
            confirmButton = {
                Button(
                    onClick = {
                        if (!quizSubmitted) {
                            quizSubmitted = true
                        } else {
                            activeQuizModule = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                ) {
                    Text(if (quizSubmitted) "Done" else "Submit Answer")
                }
            },
            dismissButton = {
                if (!quizSubmitted) {
                    OutlinedButton(onClick = { activeQuizModule = null }) {
                        Text("Cancel")
                    }
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Quiz, contentDescription = null, tint = Color(0xFF0F766E))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CPD Assessment Quiz", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(mod.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F766E))
                    Text(
                        text = "Question: In mobile outreach settings with limited power, what is the primary clinical advantage of Atraumatic Restorative Treatment (ART)?",
                        fontSize = 12.sp,
                        color = Color(0xFF1E293B)
                    )

                    val options = listOf(
                        "Removes need for rotary handpieces and electricity by using hand excavation & high-viscosity glass ionomer cement.",
                        "Requires high-voltage laser equipment.",
                        "Replaces all oral surgeries with antibiotics.",
                        "Requires general anesthesia for all adult patients."
                    )

                    options.forEachIndexed { index, option ->
                        val isSelected = quizAnswerSelected == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFCCFBF1) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color(0xFF0F766E) else Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { if (!quizSubmitted) quizAnswerSelected = index }
                        ) {
                            Text(
                                text = "${('A' + index)}. $option",
                                fontSize = 11.sp,
                                modifier = Modifier.padding(8.dp),
                                color = if (isSelected) Color(0xFF0F766E) else Color(0xFF334155),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    if (quizSubmitted) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDCFCE7),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "✅ Correct! Score: 100%. +${mod.points} CPD Credits credited to your Rwanda Dental Registry profile.",
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }
}
