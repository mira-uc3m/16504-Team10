package com.example.homebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homebase.data.view.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3F51B5)) // Dark blue background from screenshot
    ) {
        // Top Header
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color.White)
            Text(
                "My Schedule",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Calendar Card (Simulated for iteration 1)
        CalendarSection(viewModel)

        Spacer(modifier = Modifier.weight(1f))

        // Bottom List Section
        if (viewModel.filteredActivities.isNotEmpty()) {
            ActivityListSection(viewModel)
        } else {
            // "Add to Schedule" button for empty days
            Button(
                onClick = { /* Handle Add */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF3F51B5))
                Text("Add to Schedule", color = Color(0xFF3F51B5))
            }
        }
    }
}

@Composable
fun ActivityListSection(viewModel: ScheduleViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = viewModel.selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                style = MaterialTheme.typography.labelLarge,
                color = Color.Gray
            )

            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(viewModel.filteredActivities) { activity ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(activity.color), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            // Icon placeholder
                        }
                        Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                            Text(activity.name, fontWeight = FontWeight.Bold)
                            Text(activity.room, color = Color.Gray, fontSize = 12.sp)
                        }
                        Text(activity.time, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                    }
                }
            }

            Button(
                onClick = { /* Handle Add */ },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Add to Schedule")
            }
        }
    }
}

@Composable
fun CalendarSection(viewModel: ScheduleViewModel) {
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                Text("Sep 2025", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day Labels (Su, Mo, Tu...)
            val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                daysOfWeek.forEach { day ->
                    Text(day, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            // The Actual Date Grid
            // We use 7 columns for the 7 days of the week
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(250.dp).padding(top = 8.dp),
                userScrollEnabled = false // Keep it static like a calendar card
            ) {
                // Assuming September 2025 starts on a Monday (Index 1)
                // We add empty items for the "offset" so the 1st starts on the right day
                items(1) { Spacer(modifier = Modifier.size(32.dp)) }

                items(30) { index ->
                    val dayNumber = index + 1
                    val date = LocalDate.of(2025, 9, dayNumber)
                    val isSelected = viewModel.selectedDate == date

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectedDate = date }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) Color(0xFF3F51B5) else Color.Transparent,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$dayNumber",
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        // The activity indicator dot
                        if (viewModel.hasActivity(date)) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(4.dp)
                                    .background(Color(0xFF3F51B5), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}