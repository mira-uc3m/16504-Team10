package com.example.homebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassListScreen(
    navController: NavHostController,
    viewModel: ScheduleViewModel = viewModel()
) {
    var currentMonday by remember {
        mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }
    
    val endDate = LocalDate.of(2025, 5, 30)
    val headerFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
    val weekRangeFormatter = DateTimeFormatter.ofPattern("MMM d")

    val daysOfWeek = listOf(
        currentMonday,
        currentMonday.plusDays(1),
        currentMonday.plusDays(2),
        currentMonday.plusDays(3),
        currentMonday.plusDays(4)
    )

    // Helper to check if a date is within the semester
    fun isWithinSemester(date: LocalDate) = !date.isAfter(endDate)

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFFE8F1F2))) {
                TopAppBar(
                    title = {
                        Text(
                            "Class List",
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back",
                                tint = Color(0xFF333333),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFE8F1F2)
                    )
                )
                
                // Week Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonday = currentMonday.minusWeeks(1) }) {
                        Icon(Icons.Default.ArrowBackIos, contentDescription = "Prev Week", tint = Color(0xFF3022A6), modifier = Modifier.size(20.dp))
                    }
                    
                    Text(
                        text = "${currentMonday.format(weekRangeFormatter)} - ${currentMonday.plusDays(4).format(weekRangeFormatter)}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3022A6),
                        fontSize = 16.sp
                    )
                    
                    IconButton(onClick = { currentMonday = currentMonday.plusWeeks(1) }) {
                        Icon(Icons.Default.ArrowForwardIos, contentDescription = "Next Week", tint = Color(0xFF3022A6), modifier = Modifier.size(20.dp))
                    }
                }
                HorizontalDivider(thickness = 0.5.dp, color = Color.Gray.copy(alpha = 0.1f))
            }
        },
        containerColor = Color(0xFFE8F1F2)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            daysOfWeek.forEach { date ->
                // Use the ViewModel's check to get real classes for each date
                val dayClasses = viewModel.allActivities.filter { viewModel.isEventOnDate(it, date) }
                
                if (isWithinSemester(date) && dayClasses.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = date.format(headerFormatter),
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    items(dayClasses) { event ->
                        ClassRow(event) {
                            navController.navigate("schedule_screen")
                        }
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = Color.Gray.copy(alpha = 0.1f)
                        )
                    }
                } else if (date.dayOfWeek != DayOfWeek.SATURDAY && date.dayOfWeek != DayOfWeek.SUNDAY && !isWithinSemester(date)) {
                    item {
                        if (date == currentMonday) {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                                Text("No classes scheduled after May 30", color = Color.Gray)
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(40.dp))
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
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(event.color)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = displayIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = event.location,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Text(
            text = event.time,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3022A6),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
