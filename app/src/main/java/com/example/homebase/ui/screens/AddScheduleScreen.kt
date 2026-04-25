package com.example.homebase.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homebase.data.view.AddScheduleViewModel
import com.example.homebase.data.view.ScheduleViewModel
import com.example.homebase.data.model.DateEntry
import com.example.homebase.data.model.ScheduleEvent
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    onBack: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    viewModel: AddScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add Schedule",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3022A6)
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text("Subject Name", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = viewModel.className,
                    onValueChange = { viewModel.className = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Subject name...") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Label & Color", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                
                // Color Picker
                Row(Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(0xFF332CA4L, 0xFFF06292L, 0xFFFFB74DL, 0xFF81C784L, 0xFF4FC3F7L).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(color), CircleShape)
                                .clickable { viewModel.selectedColor = color }
                                .border(if (viewModel.selectedColor == color) 2.dp else 0.dp, Color.Gray, CircleShape)
                        )
                    }
                }

                Text("Icon", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                Row(Modifier.padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    viewModel.icons.forEachIndexed { index, icon ->
                        Icon(
                            icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { viewModel.selectedIconIndex = index }
                                .border(if(viewModel.selectedIconIndex == index) 1.dp else 0.dp, Color.Blue),
                            tint = if(viewModel.selectedIconIndex == index) Color.Black else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Location", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = viewModel.location,
                    onValueChange = { viewModel.location = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search location...") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color(0xFFF5F5F5))
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            itemsIndexed(viewModel.dateEntries) { index, entry ->
                var dateDisplay by remember(entry.day, entry.month, entry.year) {
                    mutableStateOf(if (entry.day.isEmpty()) "Choose Date" else "${entry.month}/${entry.day}/${entry.year}")
                }
                var timeDisplay by remember(entry.time) {
                    mutableStateOf(entry.time)
                }
                var repeatMode by remember(entry.repeat) {
                    mutableStateOf(entry.repeat)
                }

                DateEntryRow(
                    dateText = dateDisplay,
                    timeText = timeDisplay,
                    repeatMode = repeatMode,
                    onDateClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { _, y, m, d ->
                                entry.year = y.toString()
                                entry.month = (m + 1).toString()
                                entry.day = d.toString()
                                dateDisplay = "${m + 1}/$d/$y"
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    },
                    onTimeClick = {
                        val calendar = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, h, min ->
                                val time = LocalTime.of(h, min)
                                val formatted = time.format(DateTimeFormatter.ofPattern("hh:mm a"))
                                entry.time = formatted
                                timeDisplay = formatted
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        ).show()
                    },
                    onRepeatChange = { 
                        entry.repeat = it 
                        repeatMode = it
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextButton(onClick = { viewModel.addDateRow() }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Date")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val events = viewModel.dateEntries.filter { it.day.isNotEmpty() }.map { entry ->
                            ScheduleEvent(
                                id = UUID.randomUUID().toString(),
                                userId = currentUserId, // CRITICAL: Added userId
                                name = viewModel.className,
                                location = viewModel.location,
                                time = entry.time,
                                date = "${entry.year}-${entry.month.padStart(2, '0')}-${entry.day.padStart(2, '0')}",
                                color = viewModel.selectedColor,
                                iconIndex = viewModel.selectedIconIndex,
                                repeatType = entry.repeat
                            )
                        }
                        if (events.isNotEmpty()) {
                            scheduleViewModel.saveEventsToFirebase(events)
                            onBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3022A6))
                ) {
                    Text("Add Classes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun DateEntryRow(
    dateText: String,
    timeText: String,
    repeatMode: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onRepeatChange: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Date", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .clickable { onDateClick() }
                        .padding(12.dp)
                ) {
                    Text(dateText)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Time", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
                        .clickable { onTimeClick() }
                        .padding(12.dp)
                ) {
                    Text(timeText)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text("Repeat", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Never", "Daily", "Weekly").forEach { option ->
                FilterChip(
                    selected = repeatMode == option,
                    onClick = { onRepeatChange(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}
