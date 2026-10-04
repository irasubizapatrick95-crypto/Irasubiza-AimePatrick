package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.viewmodel.AppLanguage
import com.example.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalTopAppBar(
    currentScreen: AppScreen,
    currentLanguage: AppLanguage,
    onSelectLanguage: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLangMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = "Kalahari Dental Logo",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.foundation.layout.Column {
                    Text(
                        text = "Kalahari Dental",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "AI Mobile Clinic • Rwanda 🇷🇼",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFCCFBF1),
                        fontSize = 11.sp
                    )
                }
            }
        },
        actions = {
            // Language selector button
            Box {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier
                        .clickable { showLangMenu = true }
                        .padding(horizontal = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentLanguage) {
                                AppLanguage.EN -> "EN"
                                AppLanguage.RW -> "RW"
                                AppLanguage.FR -> "FR"
                            },
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
                DropdownMenu(
                    expanded = showLangMenu,
                    onDismissRequest = { showLangMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("English (EN)") },
                        onClick = {
                            onSelectLanguage(AppLanguage.EN)
                            showLangMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Ikinyarwanda (RW)") },
                        onClick = {
                            onSelectLanguage(AppLanguage.RW)
                            showLangMenu = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Français (FR)") },
                        onClick = {
                            onSelectLanguage(AppLanguage.FR)
                            showLangMenu = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Rapid Emergency Call Button (+250 735 204 584 from Kalahari pitch)
            Surface(
                shape = CircleShape,
                color = Color(0xFFDC2626),
                modifier = Modifier
                    .size(38.dp)
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:+250735204584")
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = "Emergency Hotline Call",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0F766E),
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        modifier = modifier.testTag("dental_top_bar")
    )
}

@Composable
fun DentalBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = modifier.testTag("dental_bottom_bar")
    ) {
        NavigationBarItem(
            selected = currentScreen == AppScreen.HOME,
            onClick = { onNavigate(AppScreen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F766E),
                indicatorColor = Color(0xFFCCFBF1)
            ),
            modifier = Modifier.testTag("nav_home")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.MAP_LOCATOR,
            onClick = { onNavigate(AppScreen.MAP_LOCATOR) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.MAP_LOCATOR) Icons.Filled.Map else Icons.Outlined.Map,
                    contentDescription = "Map & Vans"
                )
            },
            label = { Text("Map", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F766E),
                indicatorColor = Color(0xFFCCFBF1)
            ),
            modifier = Modifier.testTag("nav_map")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.APPOINTMENTS || currentScreen == AppScreen.BOOKING,
            onClick = { onNavigate(AppScreen.APPOINTMENTS) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.APPOINTMENTS) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                    contentDescription = "Appointments"
                )
            },
            label = { Text("Visits", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F766E),
                indicatorColor = Color(0xFFCCFBF1)
            ),
            modifier = Modifier.testTag("nav_visits")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.AI_SCREENING,
            onClick = { onNavigate(AppScreen.AI_SCREENING) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.AI_SCREENING) Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome,
                    contentDescription = "AI Dental"
                )
            },
            label = { Text("AI Scan", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F766E),
                indicatorColor = Color(0xFFCCFBF1)
            ),
            modifier = Modifier.testTag("nav_ai_scan")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.MEDICAL_HISTORY,
            onClick = { onNavigate(AppScreen.MEDICAL_HISTORY) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.MEDICAL_HISTORY) Icons.Filled.MedicalServices else Icons.Outlined.MedicalServices,
                    contentDescription = "History"
                )
            },
            label = { Text("History", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F766E),
                indicatorColor = Color(0xFFCCFBF1)
            ),
            modifier = Modifier.testTag("nav_history")
        )

        NavigationBarItem(
            selected = currentScreen == AppScreen.CLINICIAN_WORKSPACE,
            onClick = { onNavigate(AppScreen.CLINICIAN_WORKSPACE) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == AppScreen.CLINICIAN_WORKSPACE) Icons.Filled.School else Icons.Outlined.School,
                    contentDescription = "Clinician & CPD"
                )
            },
            label = { Text("CPD Hub", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0F766E),
                indicatorColor = Color(0xFFCCFBF1)
            ),
            modifier = Modifier.testTag("nav_cpd")
        )
    }
}
