package com.example.homebase.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.homebase.data.model.CampusData
import com.example.homebase.data.model.CampusBuilding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlin.math.*

@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var selectedCampus by remember { mutableStateOf("Leganes") }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<Location?>(null) }
    
    // Permission state that refreshes when screen is resumed
    var hasLocationPermission by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                                       ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Request active location updates to ensure real-time tracking works
    DisposableEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMinUpdateIntervalMillis(2000L)
                .build()
                
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    userLocation = result.lastLocation
                }
            }
            
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } catch (e: SecurityException) {}
            
            onDispose {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            }
        } else {
            onDispose {}
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CampusData.leganesPos, 16f)
    }

    // Initial centering on user when detected
    var hasCenteredOnUser by remember { mutableStateOf(false) }
    LaunchedEffect(userLocation) {
        if (userLocation != null && !hasCenteredOnUser) {
            val userLatLng = LatLng(userLocation!!.latitude, userLocation!!.longitude)
            cameraPositionState.animate(
                com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLatLng, 16f)
            )
            hasCenteredOnUser = true
        }
    }

    LaunchedEffect(selectedCampus) {
        // Only move camera if user location hasn't been set yet or if user manually toggles campus
        val targetPos = if (selectedCampus == "Leganes") CampusData.leganesPos else CampusData.getafePos
        cameraPositionState.animate(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(targetPos, 16f)
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        MapHeader(navController, selectedCampus) { selectedCampus = it }

        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = hasLocationPermission
                ),
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                )
            ) {
                val currentBuildings = if (selectedCampus == "Leganes")
                    CampusData.leganesBuildings else CampusData.getafeBuildings

                currentBuildings.forEach { building ->
                    Marker(
                        state = MarkerState(position = building.position),
                        title = building.name
                    )
                }
            }
        }

        BuildingListSection(selectedCampus, userLocation)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            containerColor = if (isSelected) Color(0xFF3022A6) else Color(0xFFBDBDBD)
        )
    ) {
        Text(label, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BuildingListSection(campus: String, userLocation: Location?) {
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

        LazyColumn(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            items(buildings) { building ->
                val calculatedDistance = if (userLocation != null) {
                    calculateDistance(userLocation.latitude, userLocation.longitude, building.position.latitude, building.position.longitude)
                } else {
                    building.distance 
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
                        tint = Color(0xFF3022A6),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        building.name,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        calculatedDistance,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): String {
    val r = 6371
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = r * c

    return if (distance < 1) {
        "${(distance * 1000).roundToInt()} m"
    } else {
        "${"%.1f".format(distance)} km"
    }
}
