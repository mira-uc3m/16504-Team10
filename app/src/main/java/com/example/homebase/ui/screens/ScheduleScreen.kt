package com.example.homebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
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
import com.example.homebase.ui.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.navigation.NavController

@Composable
fun ScheduleScreen(navController: NavController, viewModel: ScheduleViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.fetchScheduleFromFirebase()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3F51B5))
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

        CalendarSection(viewModel)

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Class List Section
        if (viewModel.filteredActivities.isNotEmpty()) {
            ActivityListSection(navController, viewModel)
        } else {
            // "Add to Schedule" button for empty days
            Button(
                onClick = { navController.navigate(Screen.AddSchedule.route) }, // TODO: add functionality --> iteration 2
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
fun ActivityListSection(navController: NavController, viewModel: ScheduleViewModel) {
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
                            Text(activity.location, color = Color.Gray, fontSize = 12.sp)
                        }
                        Text(activity.time, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5))
                    }
                }
            }

            Button(
                onClick = { navController.navigate(Screen.AddSchedule.route) },
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
    val currentMonth = viewModel.currentMonth
    val daysInMonth = currentMonth.lengthOfMonth()

    // Find what day of the week the 1st falls on
    // .value returns 1 (Mon) to 7 (Sun). We adjust so Sun = 0.
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7
    Card(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month Navigation Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                }

                Text(
                    text = "${currentMonth.month.name.lowercase().capitalize()} ${currentMonth.year}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                }
            }

            // Day Labels
            val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                daysOfWeek.forEach { day ->
                    Text(day, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            // Dynamic Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(260.dp).padding(top = 8.dp),
                userScrollEnabled = false
            ) {
                // Empty spacers for the start of the month
                items(firstDayOfWeekIndex) { Spacer(modifier = Modifier.size(32.dp)) }

                // Add the actual days
                items(daysInMonth) { index ->
                    val dayNum = index + 1
                    val date = currentMonth.atDay(dayNum)
                    val isSelected = viewModel.selectedDate == date
                    val isToday = date == LocalDate.now()

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { viewModel.selectedDate = date }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isSelected) Color(0xFF3F51B5) else Color.Transparent,
                            border = if (isToday && !isSelected) BorderStroke(1.dp, Color(0xFF3F51B5)) else null,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "$dayNum",
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontSize = 14.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
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