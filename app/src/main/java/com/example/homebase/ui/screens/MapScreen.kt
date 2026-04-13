package com.example.homebase.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homebase.data.model.CampusData
import com.example.homebase.data.model.CampusBuilding
import com.example.homebase.data.view.SettingsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.*

@Composable
fun MapScreen(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedCampus by remember { mutableStateOf("Leganes") }
    var showEnableLocationDialog by remember { mutableStateOf(false) }

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.any { it }
        if (hasLocationPermission) {
            settingsViewModel.toggleLocationTracking(true)
        } else {
            settingsViewModel.toggleLocationTracking(false)
        }
    }

    // Mocked current user location (e.g., somewhere in Leganes)
    // In a real app, this would be updated via FusedLocationProviderClient
    val userLocation = remember { LatLng(40.3330, -3.7635) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CampusData.leganesPos, 15f)
    }

    LaunchedEffect(selectedCampus) {
        val targetPos = if (selectedCampus == "Leganes") CampusData.leganesPos else CampusData.getafePos
        cameraPositionState.animate(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(targetPos, 16f)
        )
    }

    if (showEnableLocationDialog) {
        AlertDialog(
            onDismissRequest = { showEnableLocationDialog = false },
            title = { Text("Enable Location?") },
            text = { Text("Location tracking is disabled in settings. Would you like to enable it to see your position on the map?") },
            confirmButton = {
                TextButton(onClick = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    showEnableLocationDialog = false
                }) { Text("Enable") }
            },
            dismissButton = {
                TextButton(onClick = { showEnableLocationDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        MapHeader(navController, selectedCampus) { selectedCampus = it }

        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                properties = MapProperties(isMyLocationEnabled = settingsViewModel.locationTrackingEnabled && hasLocationPermission)
            ) {
                val currentBuildings = if (selectedCampus == "Leganes")
                    CampusData.leganesBuildings else CampusData.getafeBuildings

                currentBuildings.forEach { building ->
                    Marker(
                        state = MarkerState(position = building.position),
                        title = building.name
                    )
                }

                // If location is enabled, we show a marker for the user as well (or use isMyLocationEnabled)
                if (settingsViewModel.locationTrackingEnabled && hasLocationPermission) {
                    Marker(
                        state = MarkerState(position = userLocation),
                        title = "You are here",
                        icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                            com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE
                        )
                    )
                }
            }

            // Location FAB
            FloatingActionButton(
                onClick = {
                    if (!hasLocationPermission) {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    } else if (!settingsViewModel.locationTrackingEnabled) {
                        showEnableLocationDialog = true
                    } else {
                        cameraPositionState.move(
                            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLocation, 17f)
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color.White,
                contentColor = Color(0xFF3F51B5)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }
        }

        BuildingListSection(selectedCampus, settingsViewModel.locationTrackingEnabled && hasLocationPermission, userLocation)
    }
}

@Composable
fun MapHeader(navController: NavHostController, selected: String, onToggle: (String) -> Unit) {
    Column(modifier = Modifier.padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Campus Map",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampusToggleButton(
                label = "Leganes",
                isSelected = selected == "Leganes",
                modifier = Modifier.weight(1f)
            ) { onToggle("Leganes") }

            CampusToggleButton(
                label = "Getafe",
                isSelected = selected == "Getafe",
                modifier = Modifier.weight(1f)
            ) { onToggle("Getafe") }
        }
    }
}

@Composable
fun CampusToggleButton(label: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF3F51B5) else Color(0xFFBDBDBD)
        )
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BuildingListSection(campus: String, locationEnabled: Boolean, userPos: LatLng) {
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search location...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Clear") },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5))
        )

        Spacer(modifier = Modifier.height(16.dp))

        val buildings = if (campus == "Leganes") CampusData.leganesBuildings else CampusData.getafeBuildings

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(buildings) { building ->
                val distanceText = if (locationEnabled) {
                    calculateDistance(userPos, building.position)
                } else {
                    "--"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF3F51B5),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        building.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        distanceText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}

fun calculateDistance(p1: LatLng, p2: LatLng): String {
    val r = 6371 // Earth radius in km
    val dLat = Math.toRadians(p2.latitude - p1.latitude)
    val dLon = Math.toRadians(p2.longitude - p1.longitude)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(p1.latitude)) * cos(Math.toRadians(p2.latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = r * c

    return if (distance < 1) {
        "${(distance * 1000).toInt()} m"
    } else {
        String.format("%.1f km", distance)
    }
}
