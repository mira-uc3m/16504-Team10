package com.example.homebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    var pushNotifications by remember { mutableStateOf(true) }
    var classReminders by remember { mutableStateOf(false) }
    var checklistReminders by remember { mutableStateOf(true) }
    var locationTracking by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HOME BASE", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3629B7)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // --- SECTION 1: LANGUAGE & REGION ---
            Text(
                "Language & Region",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsDropdownItem(label = "Region", value = "Canada 🇨🇦")
            SettingsDropdownItem(label = "Language", value = "English")
            SettingsDropdownItem(label = "Preferred Currency", value = "CAD")

            // Super thin horizontal grey line
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 2: NOTIFICATIONS ---
            Text(
                "Notifications",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsSwitchItem("Allow Push Notifications", pushNotifications) { pushNotifications = it }
            SettingsSwitchItem("Class Reminders", classReminders) { classReminders = it }
            SettingsSwitchItem("Checklist Reminders", checklistReminders) { checklistReminders = it }

            // Super thin horizontal grey line
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 3: LOCATION & MAP PREFERENCES ---
            Text(
                "Location & Map Preferences",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            SettingsSwitchItem("Enable Location Tracking", locationTracking) { locationTracking = it }
        }
    }
}

@Composable
fun SettingsDropdownItem(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Card(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(value, color = Color.Black)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF3629B7))
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Black)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color(0xFF3629B7),
                checkedThumbColor = Color.White
            )
        )
    }
}