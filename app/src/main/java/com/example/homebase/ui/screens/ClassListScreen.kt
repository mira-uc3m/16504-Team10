package com.example.homebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homebase.data.model.ScheduleEvent
import com.example.homebase.data.view.ScheduleViewModel
import com.example.homebase.ui.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListScreen(
    navController: NavHostController,
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
    // Current week adjustment
    var weekOffset by remember { mutableStateOf(0) }
    
    val today = LocalDate.now().plusWeeks(weekOffset.toLong())
    val startOfWeek = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
    val endOfWeek = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
    
    val dateFormatter = DateTimeFormatter.ofPattern("MMMM d")
    val weekRangeText = "Week of ${startOfWeek.format(dateFormatter)} - ${endOfWeek.format(dateFormatter)}"

    val allActivities = scheduleViewModel.allActivities

    // Filter events for the current week and group them by day
    val weeklyClasses = remember(allActivities, weekOffset) {
        val days = (0..6).map { startOfWeek.plusDays(it.toLong()) }
        days.associateWith { day ->
            allActivities.filter { event ->
                isEventOnDate(event, day)
            }.sortedBy { it.time }
        }.filter { it.value.isNotEmpty() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Class List",
                        color = Color(0xFF333333),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color(0xFF333333),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Week Selection Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { weekOffset-- }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Week", tint = Color(0xFF3022A6))
                }
                
                Text(
                    text = weekRangeText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF3022A6)
                )

                IconButton(onClick = { weekOffset++ }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Week", tint = Color(0xFF3022A6))
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))

            if (weeklyClasses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No classes scheduled for this week.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    weeklyClasses.forEach { (day, classes) ->
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = day.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        }
                        items(classes) { event ->
                            ClassRow(event) {
                                // Link to Schedule Page: set the selected date in ViewModel and navigate
                                scheduleViewModel.selectedDate = day
                                navController.navigate("schedule_screen")
                            }
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = Color.LightGray.copy(alpha = 0.2f)
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ClassRow(event: ScheduleEvent, onClick: () -> Unit) {
    val icons = listOf(
        Icons.Default.Stars,
        Icons.Default.Thunderstorm,
        Icons.Default.Notes,
        Icons.Default.Train,
        Icons.Default.List,
        Icons.Default.School
    )
    val displayIcon = icons.getOrElse(event.iconIndex) { Icons.Default.School }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(event.color)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = displayIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.name,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = "${event.time} • ${event.location}",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
        
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = "View Details",
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun isEventOnDate(event: ScheduleEvent, targetDate: LocalDate): Boolean {
    return try {
        val eventDate = LocalDate.parse(event.date)
        if (targetDate.isBefore(eventDate)) return false
        if (event.endDate != null && targetDate.isAfter(LocalDate.parse(event.endDate))) return false
        
        when (event.repeatType) {
            "Never" -> targetDate == eventDate
            "Daily" -> true
            "Weekly" -> targetDate.dayOfWeek == eventDate.dayOfWeek
            else -> targetDate == eventDate
        }
    } catch (e: Exception) {
        false
    }
}
