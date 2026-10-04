package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.viewmodel.AppScreen
import com.example.viewmodel.DentalViewModel

@Composable
fun AIScreeningScreen(
    viewModel: DentalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Tab 0: AI Shadow-Mode Oral Screening Triage
    var complaint by remember { mutableStateOf("Sharp discomfort on lower right tooth when chewing and cold sensitivity") }
    var painScore by remember { mutableFloatStateOf(6f) }
    var durationDays by remember { mutableFloatStateOf(4f) }
    var hasBleeding by remember { mutableStateOf(false) }
    var hasSwelling by remember { mutableStateOf(false) }
    var hasSensitivity by remember { mutableStateOf(true) }
    var hasChewingDifficulty by remember { mutableStateOf(true) }

    val isAnalyzing by viewModel.isAnalyzingScreening.collectAsStateWithLifecycle()
    val lastResult by viewModel.lastScreeningResult.collectAsStateWithLifecycle()

    // Tab 1: AI Image Studio
    var promptText by remember { mutableStateOf("High-resolution 3D anatomical cross-section diagram of human tooth showing healthy enamel, dentin, and dental pulp nerve") }
    var selectedSize by remember { mutableStateOf("2K") } // "1K", "2K", "4K"
    var isEditingMode by remember { mutableStateOf(false) }

    val isGeneratingImage by viewModel.isGeneratingImage.collectAsStateWithLifecycle()
    val generatedImageResult by viewModel.generatedImageResult.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_screening_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = Color(0xFF0F766E)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Oral Health Screening", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_ai_screening")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("AI Image Studio", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp)) },
                modifier = Modifier.testTag("tab_ai_image_studio")
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedTab == 0) {
                // Tab 0: AI Oral Screening & Shadow Mode Triage
                item {
                    Column {
                        Text(
                            text = "AI Oral Screening & Shadow Mode",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "From Kalahari Dental Care solution: guided questionnaires to flag concerns while ensuring clinician oversight.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Chief Complaint
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "1. Chief Complaint & Symptoms",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E),
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = complaint,
                                onValueChange = { complaint = it },
                                label = { Text("Describe what hurts or feels abnormal") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                maxLines = 3
                            )
                        }
                    }
                }

                // Pain Score & Duration
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pain Scale (VAS 1 - 10):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "${painScore.toInt()} / 10 ${if (painScore >= 8) "🔴 Severe" else if (painScore >= 5) "🟡 Moderate" else "🟢 Mild"}",
                                    fontWeight = FontWeight.Bold,
                                    color = if (painScore >= 8) Color(0xFFDC2626) else Color(0xFF0F766E),
                                    fontSize = 13.sp
                                )
                            }
                            Slider(
                                value = painScore,
                                onValueChange = { painScore = it },
                                valueRange = 1f..10f,
                                steps = 8,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF0F766E),
                                    activeTrackColor = Color(0xFF0F766E)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Duration of Symptoms:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${durationDays.toInt()} days", fontWeight = FontWeight.Bold, color = Color(0xFF0284C7), fontSize = 13.sp)
                            }
                            Slider(
                                value = durationDays,
                                onValueChange = { durationDays = it },
                                valueRange = 1f..30f,
                                steps = 28,
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF0284C7),
                                    activeTrackColor = Color(0xFF0284C7)
                                )
                            )
                        }
                    }
                }

                // Symptom Switches
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Clinical Check Flags:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F766E))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Bleeding Gums (Gingival hemorrhage)", fontSize = 13.sp)
                                Switch(
                                    checked = hasBleeding,
                                    onCheckedChange = { hasBleeding = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0F766E))
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Facial or Gum Swelling (Abscess)", fontSize = 13.sp)
                                Switch(
                                    checked = hasSwelling,
                                    onCheckedChange = { hasSwelling = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFFDC2626))
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Hot / Cold Temperature Sensitivity", fontSize = 13.sp)
                                Switch(
                                    checked = hasSensitivity,
                                    onCheckedChange = { hasSensitivity = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0F766E))
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Difficulty Chewing / Biting Pain", fontSize = 13.sp)
                                Switch(
                                    checked = hasChewingDifficulty,
                                    onCheckedChange = { hasChewingDifficulty = it },
                                    colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF0F766E))
                                )
                            }
                        }
                    }
                }

                // CTA
                item {
                    Button(
                        onClick = {
                            viewModel.runAIScreening(
                                complaint = complaint,
                                painScore = painScore.toInt(),
                                durationDays = durationDays.toInt(),
                                hasBleeding = hasBleeding,
                                hasSwelling = hasSwelling,
                                hasSensitivity = hasSensitivity,
                                hasDifficultyChewing = hasChewingDifficulty
                            )
                        },
                        enabled = !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_run_ai_screening"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Running Shadow-Mode AI Analysis...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Run AI Oral Screening Triage", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Results Card
                lastResult?.let { result ->
                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = when (result.riskLevel) {
                                    "URGENT" -> Color(0xFFFEF2F2)
                                    "HIGH" -> Color(0xFFFFFBEB)
                                    else -> Color(0xFFF0FDF4)
                                }
                            ),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.testTag("ai_screening_result_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = when (result.riskLevel) {
                                            "URGENT" -> Color(0xFFDC2626)
                                            "HIGH" -> Color(0xFFF59E0B)
                                            else -> Color(0xFF16A34A)
                                        }
                                    ) {
                                        Text(
                                            text = "Triage: ${result.riskLevel}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Text(
                                        text = "AI Confidence: ${(result.shadowModeConfidence * 100).toInt()}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F766E)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Probable Finding:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = result.probableCondition,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Recommended Clinical Action:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = result.recommendation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF334155)
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🛡️ ${result.triageProtocol}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF0F766E),
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.BOOKING) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Schedule Clinic / Mobile Van Triage")
                                }
                            }
                        }
                    }
                }
            } else {
                // Tab 1: AI Image Studio
                // Feature 1: Create & Edit Images using gemini-3.1-flash-image-preview
                // Feature 2: Generate High Quality Images using gemini-3-pro-image-preview (1K, 2K, 4K)
                item {
                    Column {
                        Text(
                            text = "AI Dental Image Studio",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "Generate and preview high-definition oral health simulations, clinical anatomy, and aesthetic restorations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Mode Selector: gemini-3.1-flash-image-preview vs gemini-3-pro-image-preview
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !isEditingMode,
                            onClick = { isEditingMode = false },
                            label = { Text("High-Quality 1K/2K/4K (Pro)", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.HighQuality, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0F766E),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("chip_mode_pro")
                        )

                        FilterChip(
                            selected = isEditingMode,
                            onClick = { isEditingMode = true },
                            label = { Text("Create & Edit (Flash)", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0284C7),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).testTag("chip_mode_flash")
                        )
                    }
                }

                // Affordance for User-Specified Image Size (1K, 2K, 4K) for gemini-3-pro-image-preview
                if (!isEditingMode) {
                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF0FDF4))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Resolution Affordance (gemini-3-pro-image-preview):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    listOf("1K", "2K", "4K").forEach { size ->
                                        val isSelected = selectedSize == size
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) Color(0xFF0F766E) else Color.White,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F766E)),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { selectedSize = size }
                                                .testTag("resolution_chip_$size")
                                        ) {
                                            Box(
                                                modifier = Modifier.padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "$size Resolution",
                                                    color = if (isSelected) Color.White else Color(0xFF0F766E),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Text Prompt Field
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isEditingMode) "Text Prompt for Image Creation / Editing:" else "Text Prompt for High-Res Generation:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0F766E)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = promptText,
                                onValueChange = { promptText = it },
                                modifier = Modifier.fillMaxWidth().testTag("input_ai_image_prompt"),
                                shape = RoundedCornerShape(10.dp),
                                maxLines = 4
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Sample Clinical Dental Presets:", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            val presets = listOf(
                                "3D cross-section diagram of tooth root canal & pulp chamber",
                                "Before and after aesthetic smile whitening and scaling simulation",
                                "Mobile dental clinic operatory setup inside van in Rwanda"
                            )
                            presets.forEach { preset ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clickable { promptText = preset }
                                ) {
                                    Text(
                                        text = "✦ $preset",
                                        fontSize = 11.sp,
                                        color = Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Generate Button
                item {
                    Button(
                        onClick = {
                            if (isEditingMode) {
                                viewModel.createOrEditDentalImage(promptText)
                            } else {
                                viewModel.generateHighResDentalImage(promptText, selectedSize)
                            }
                        },
                        enabled = !isGeneratingImage && promptText.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_generate_ai_image"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEditingMode) Color(0xFF0284C7) else Color(0xFF0F766E)
                        )
                    ) {
                        if (isGeneratingImage) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isEditingMode) "Processing Image..." else "Generating $selectedSize Image...")
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEditingMode) "Create/Edit Image (gemini-3.1-flash)" else "Generate High-Res ($selectedSize)",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Image Output Viewer
                generatedImageResult?.let { result ->
                    item {
                        ElevatedCard(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp),
                            modifier = Modifier.testTag("generated_image_card")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFF0F766E).copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = result.modelUsed,
                                            color = Color(0xFF0F766E),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Text(
                                        text = "${result.resolution} • Ready",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0284C7)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                result.bitmap?.let { bmp ->
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = "Generated Dental Image",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = result.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF475569)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
