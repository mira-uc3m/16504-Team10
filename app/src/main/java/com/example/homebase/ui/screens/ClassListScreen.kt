package com.example.homebase.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homebase.R
import com.example.homebase.data.model.ScheduleEvent
import com.example.homebase.data.view.ScheduleViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ClassListScreen(
    navController: NavHostController,
    viewModel: ScheduleViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchScheduleFromFirebase()
    }

    var currentPage by remember { mutableStateOf(0) }
    
    // Calculate current date and Mondays for the next 4 weeks
    val today = LocalDate.now()
    val currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    
    val dayFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)
    val weekRangeFormatter = DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)

    // Generate week titles for 4 weeks
    val weeks = (0..3).map { i ->
        val monday = currentMonday.plusWeeks(i.toLong())
        val sunday = monday.plusDays(6)
        val range = "${monday.format(weekRangeFormatter)} - ${sunday.format(weekRangeFormatter)}"
        "Week ${i + 1}: $range"
    }

    // Dynamic data generation logic from ViewModel
    fun getWeekData(startMonday: LocalDate): Map<String, List<ScheduleEvent>> {
        val data = linkedMapOf<String, List<ScheduleEvent>>()
        
        for (i in 0..4) { // Iterate Monday through Friday
            val date = startMonday.plusDays(i.toLong())
            // Only show dates from Today onwards
            if (!date.isBefore(today)) {
                val dailyEvents = viewModel.allActivities.filter { 
                    viewModel.isEventOnDate(it, date) 
                }.sortedBy { it.time }
                
                if (dailyEvents.isNotEmpty()) {
                    data[date.format(dayFormatter)] = dailyEvents
                }
            }
        }
        return data
    }

    // Get data for the currently selected week (0-3)
    val currentData = getWeekData(currentMonday.plusWeeks(currentPage.toLong()))

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
                actions = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_homebase_logo),
                        contentDescription = "Home Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                    )
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
            // Pagination Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (currentPage > 0) currentPage-- },
                    enabled = currentPage > 0
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Week", tint = if (currentPage > 0) Color(0xFF3022A6) else Color.LightGray)
                }
                
                Text(
                    text = weeks[currentPage],
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF3022A6)
                )

                IconButton(
                    onClick = { if (currentPage < weeks.size - 1) currentPage++ },
                    enabled = currentPage < weeks.size - 1
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Week", tint = if (currentPage < weeks.size - 1) Color(0xFF3022A6) else Color.LightGray)
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))

            if (currentData.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
                    val message = if (currentPage == 0) "No more classes this week!" else "No classes scheduled for this week."
                    Text(message, color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    currentData.forEach { (day, classes) ->
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = day,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        }
                        items(classes) { event ->
                            ClassRow(event)
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
fun ClassRow(event: ScheduleEvent) {
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
                text = event.location,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }

        Text(
            text = event.time,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3022A6),
            fontSize = 14.sp
        )
    }
}
