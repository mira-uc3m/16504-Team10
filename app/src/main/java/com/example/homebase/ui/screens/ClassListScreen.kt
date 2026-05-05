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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

data class UC3MClass(
    val name: String,
    val code: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ClassListScreen(navController: NavHostController) {
    var currentPage by remember { mutableStateOf(0) }
    
    // Calculate current week and next week dates
    val today = LocalDate.now()
    val currentMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val nextMonday = currentMonday.plusWeeks(1)
    
    val dayFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)
    val weekRangeFormatter = DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH)

    val week1Range = "${currentMonday.format(weekRangeFormatter)} - ${currentMonday.plusDays(6).format(weekRangeFormatter)}"
    val week2Range = "${nextMonday.format(weekRangeFormatter)} - ${nextMonday.plusDays(6).format(weekRangeFormatter)}"
    val weeks = listOf("Week 1: $week1Range", "Week 2: $week2Range")

    // Dynamic data generation logic
    fun getWeekData(startMonday: LocalDate, isWeek1: Boolean): Map<String, List<UC3MClass>> {
        val data = linkedMapOf<String, List<UC3MClass>>()
        
        // Base schedules for Week A and Week B (Week 1 and Week 2)
        val schedule1 = listOf(
            listOf( // Monday
                UC3MClass("Mobile Applications", "53461", Icons.Default.WaterDrop, Color(0xFF3022A6)),
                UC3MClass("Computer Architecture", "12345", Icons.Default.ConfirmationNumber, Color(0xFFFF4D4D)),
                UC3MClass("Operating Systems", "67890", Icons.Default.PushPin, Color(0xFF2196F3))
            ),
            listOf( // Tuesday
                UC3MClass("Software Engineering", "11111", Icons.Default.Dashboard, Color(0xFFFFB300)),
                UC3MClass("Database Systems", "22222", Icons.Default.CalendarToday, Color(0xFF4DB6AC)),
                UC3MClass("Mobile Applications", "53461", Icons.Default.WaterDrop, Color(0xFF3022A6))
            ),
            listOf( // Wednesday
                UC3MClass("Software Engineering", "11111", Icons.Default.Dashboard, Color(0xFFFFB300)),
                UC3MClass("Computer Architecture", "12345", Icons.Default.ConfirmationNumber, Color(0xFFFF4D4D)),
                UC3MClass("Operating Systems", "67890", Icons.Default.PushPin, Color(0xFF2196F3))
            ),
            listOf( // Thursday
                UC3MClass("Mobile Applications", "53461", Icons.Default.WaterDrop, Color(0xFF3022A6)),
                UC3MClass("Network Security", "99999", Icons.Default.Security, Color(0xFF9C27B0))
            ),
            listOf( // Friday
                UC3MClass("Database Systems", "22222", Icons.Default.CalendarToday, Color(0xFF4DB6AC)),
                UC3MClass("Project Management", "88888", Icons.Default.Assignment, Color(0xFF795548))
            )
        )

        val schedule2 = listOf(
            listOf( // Monday
                UC3MClass("Mobile Applications", "53461", Icons.Default.WaterDrop, Color(0xFF3022A6)),
                UC3MClass("Computer Architecture", "12345", Icons.Default.ConfirmationNumber, Color(0xFFFF4D4D))
            ),
            listOf( // Tuesday
                UC3MClass("Software Engineering", "11111", Icons.Default.Dashboard, Color(0xFFFFB300)),
                UC3MClass("Database Systems", "22222", Icons.Default.CalendarToday, Color(0xFF4DB6AC))
            ),
            listOf( // Wednesday
                UC3MClass("Operating Systems", "67890", Icons.Default.PushPin, Color(0xFF2196F3)),
                UC3MClass("Software Engineering", "11111", Icons.Default.Dashboard, Color(0xFFFFB300))
            ),
            listOf( // Thursday
                UC3MClass("Network Security", "99999", Icons.Default.Security, Color(0xFF9C27B0)),
                UC3MClass("Mobile Applications", "53461", Icons.Default.WaterDrop, Color(0xFF3022A6))
            ),
            listOf( // Friday
                UC3MClass("Project Management", "88888", Icons.Default.Assignment, Color(0xFF795548))
            )
        )

        val activeSchedule = if (isWeek1) schedule1 else schedule2

        for (i in 0..4) { // Iterate Monday through Friday
            val date = startMonday.plusDays(i.toLong())
            // Only show dates from Today onwards
            if (!date.isBefore(today)) {
                data[date.format(dayFormatter)] = activeSchedule[i]
            }
        }
        return data
    }

    // Auto-calculate data for current week and next week based on Today
    val week1Data = getWeekData(currentMonday, true)
    val week2Data = getWeekData(nextMonday, false)

    val currentData = if (currentPage == 0) week1Data else week2Data

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

            if (currentData.isEmpty() && currentPage == 0) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("No more classes this week!", color = Color.Gray, fontSize = 16.sp)
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
                        items(classes) { uc3mClass ->
                            ClassRow(uc3mClass)
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
fun ClassRow(uc3mClass: UC3MClass) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = uc3mClass.color
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = uc3mClass.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = uc3mClass.name,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = uc3mClass.code,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}
