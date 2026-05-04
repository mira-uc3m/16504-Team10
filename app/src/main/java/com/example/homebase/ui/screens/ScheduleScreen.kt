package com.example.homebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homebase.data.model.ScheduleEvent
import com.example.homebase.data.view.ScheduleViewModel
import com.example.homebase.ui.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController, viewModel: ScheduleViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        viewModel.fetchScheduleFromFirebase()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Schedule",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3022A6)
                )
            )
        },
        containerColor = Color(0xFF3022A6)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
                .background(Color(0xFF3022A6))
        ) {
            CalendarSection(viewModel)

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Class List Section - taking the rest of the space
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                if (viewModel.filteredActivities.isNotEmpty()) {
                    ActivityListSection(navController, viewModel)
                } else {
                    // Empty state
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = viewModel.selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text("No classes scheduled for today.", color = Color.Gray)
                        }
                        Button(
                            onClick = { navController.navigate(Screen.AddSchedule.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3022A6)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Schedule")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityListSection(navController: NavController, viewModel: ScheduleViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<ScheduleEvent?>(null) }

    val icons = listOf(
        Icons.Default.Stars,
        Icons.Default.Thunderstorm,
        Icons.Default.Notes,
        Icons.Default.Train,
        Icons.Default.List,
        Icons.Default.School
    )

    if (showDeleteDialog && eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Event") },
            text = { Text("Would you like to delete this specific class or the entire repeating series?") },
            confirmButton = {
                TextButton(onClick = {
                    eventToDelete?.let { viewModel.deleteEvent(it) }
                    showDeleteDialog = false
                }) {
                    Text("Delete Series", color = Color.Red)
                }
            },
            dismissButton = {
                if (eventToDelete?.repeatType != "Never") {
                    TextButton(onClick = {
                        eventToDelete?.let { viewModel.deleteSingleInstance(it, viewModel.selectedDate) }
                        showDeleteDialog = false
                    }) {
                        Text("Delete Just This One")
                    }
                }
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = viewModel.selectedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")),
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )

        LazyColumn(
            modifier = Modifier.weight(1f).padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.filteredActivities) { activity ->
                val displayIcon = icons.getOrElse(activity.iconIndex) { Icons.Default.School }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = {
                                    eventToDelete = activity
                                    showDeleteDialog = true
                                }
                            )
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(activity.color), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            displayIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(start = 16.dp).weight(1f)) {
                        Text(activity.name, fontWeight = FontWeight.Bold)
                        Text(activity.location, color = Color.Gray, fontSize = 12.sp)
                    }
                    Text(activity.time, fontWeight = FontWeight.Bold, color = Color(0xFF3022A6))
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Screen.AddSchedule.route) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3022A6)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add to Schedule")
        }
    }
}

@Composable
fun CalendarSection(viewModel: ScheduleViewModel) {
    val currentMonth = viewModel.currentMonth
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7
    
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 32.dp) // Added internal horizontal padding to fix aspect ratio
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                }

                Text(
                    text = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                }
            }

            val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                daysOfWeek.forEach { day ->
                    Text(day, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(280.dp).padding(top = 8.dp),
                userScrollEnabled = false
            ) {
                items(firstDayOfWeekIndex) { Spacer(modifier = Modifier.size(32.dp)) }

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
                            color = if (isSelected) Color(0xFF3022A6) else Color.Transparent,
                            border = if (isToday && !isSelected) BorderStroke(1.dp, Color(0xFF3022A6)) else null,
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
                                    .background(Color(0xFF3022A6), CircleShape)
                            )
                        }
                    }
                }
            }
        }
    }
}
