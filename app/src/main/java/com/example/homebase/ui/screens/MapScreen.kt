package com.example.homebase.ui.screens

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.homebase.data.model.CampusData
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*

@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current
    var selectedCampus by remember { mutableStateOf("Leganes") }
    
    // Check if location permission is granted
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(CampusData.leganesPos, 15f)
    }

    LaunchedEffect(selectedCampus) {
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

        BuildingListSection(selectedCampus)
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
fun BuildingListSection(campus: String) {
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
                        building.distance,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                HorizontalDivider(color = Color(0xFFEEEEEE))
            }
        }
    }
}
