package com.example.homebase.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Language & Region", "Notifications", "Location")
    
    // Language & Region States
    var selectedRegion by remember { mutableStateOf("Canada 🇨🇦") }
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedCurrency by remember { mutableStateOf("CAD") }

    val regions = listOf("Canada 🇨🇦", "United States 🇺🇸", "Spain 🇪🇸", "United Kingdom 🇬🇧", "France 🇫🇷", "Germany 🇩🇪", "Australia 🇦🇺")
    val languages = listOf("English", "Spanish", "French", "German", "Chinese", "Portuguese", "Italian")
    val currencies = listOf("CAD", "USD", "EUR", "GBP", "JPY", "AUD", "CNY")

    // Notification States
    var pushNotifications by remember { mutableStateOf(true) }
    var classReminders by remember { mutableStateOf(false) }
    var checklistReminders by remember { mutableStateOf(true) }

    fun hasLocPermission() = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    var locationTracking by remember { mutableStateOf(hasLocPermission()) }

    // Launcher for location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        locationTracking = granted
        if (granted) {
            Toast.makeText(context, "Location Services Activated", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3022A6)
                )
            )
        },
        containerColor = Color(0xFF3022A6)
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF3022A6),
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFF3022A6)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { 
                                Text(
                                    title, 
                                    fontSize = 13.sp, 
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) Color(0xFF3022A6) else Color.Gray
                                ) 
                            }
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 20.dp)
                ) {
                    when (selectedTab) {
                        0 -> { // Language & Region
                            Text("Language & Region", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            SettingsDropdownItem(
                                label = "Region", 
                                value = selectedRegion, 
                                options = regions,
                                onOptionSelected = { selectedRegion = it }
                            )
                            SettingsDropdownItem(
                                label = "Language", 
                                value = selectedLanguage, 
                                options = languages,
                                onOptionSelected = { selectedLanguage = it }
                            )
                            SettingsDropdownItem(
                                label = "Preferred Currency", 
                                value = selectedCurrency, 
                                options = currencies,
                                onOptionSelected = { selectedCurrency = it }
                            )
                        }
                        1 -> { // Notifications
                            Text("Notifications", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            SettingsSwitchItem("Allow Push Notifications", pushNotifications) { pushNotifications = it }
                            SettingsSwitchItem("Class Reminders", classReminders) { classReminders = it }
                            SettingsSwitchItem("Checklist Reminders", checklistReminders) { checklistReminders = it }
                        }
                        2 -> { // Location
                            Text("Location & Map Preferences", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            SettingsSwitchItem("Enable location tracking", locationTracking) { checked ->
                                if (checked) {
                                    locationPermissionLauncher.launch(
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    )
                                } else {
                                    locationTracking = false
                                    Toast.makeText(context, "Location tracking disabled", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDropdownItem(
    label: String, 
    value: String, 
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { expanded = true },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(value, color = Color.Black, fontSize = 15.sp)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF3022A6))
                }
            }
            
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color(0xFF333333), fontSize = 15.sp, fontWeight = FontWeight.Medium)
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color(0xFF3022A6),
                checkedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE0E0E0),
                uncheckedThumbColor = Color.White,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
