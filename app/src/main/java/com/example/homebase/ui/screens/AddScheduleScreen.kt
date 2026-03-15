package com.example.homebase.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.homebase.data.model.ClassActivity
import com.example.homebase.data.model.DateEntry
import com.example.homebase.data.view.AddScheduleViewModel
import com.example.homebase.data.view.ScheduleViewModel
import java.time.LocalDate
import java.util.UUID
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleScreen(
    onBack: () -> Unit,
    scheduleViewModel: ScheduleViewModel,
    viewModel: AddScheduleViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add to Schedule", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF332CA4))
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // Logic to process entries and return
                    viewModel.dateEntries.forEach { entry ->
                        try {
                            val date = LocalDate.of(entry.year.toInt(), entry.month.toInt(), entry.day.toInt())
                            scheduleViewModel.allActivities.add(
                                ClassActivity(
                                    id = UUID.randomUUID().toString(),
                                    name = viewModel.className,
                                    room = viewModel.location,
                                    time = entry.time,
                                    date = date,
                                    color = viewModel.selectedColor
                                )
                            )
                        } catch (e: Exception) { /* Handle invalid dates */ }
                    }
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF332CA4)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add to Schedule", fontSize = 16.sp)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header: Icon + Class Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF332CA4)
                ) {
                    Icon(viewModel.selectedIcon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(12.dp))
                }
                Spacer(Modifier.width(16.dp))
                OutlinedTextField(
                    value = viewModel.className,
                    onValueChange = { viewModel.className = it },
                    label = { Text("Class Name") },
                    placeholder = { Text("Input") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.Cancel, contentDescription = null) }
                )
            }

            // Color Picker
            Row(Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(0xFF332CA4, 0xFFF06292, 0xFFFFB74D, 0xFF81C784, 0xFF4FC3F7).forEach { color ->
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
                val icons = listOf(Icons.Default.Stars, Icons.Default.Thunderstorm, Icons.Default.Notes, Icons.Default.Train, Icons.Default.List)
                icons.forEach { icon ->
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { viewModel.selectedIcon = icon }
                            .border(if(viewModel.selectedIcon == icon) 1.dp else 0.dp, Color.Blue),
                        tint = if(viewModel.selectedIcon == icon) Color.Black else Color.Gray
                    )
                }
            }

            Text("Location", color = Color.Gray, style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = viewModel.location,
                onValueChange = { viewModel.location = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Value") }
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            // Date Section
            Text("Date", color = Color.Gray, style = MaterialTheme.typography.labelLarge)

            viewModel.dateEntries.forEachIndexed { index, entry ->
                DateRowItem(
                    entry = entry,
                    onDelete = {
                        // Prevent deleting the very last row so the form isn't empty
                        if (viewModel.dateEntries.size > 1) {
                            viewModel.dateEntries.removeAt(index)
                        }
                    }
                )
            }

            // Add Date Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.addDateRow() }
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Date", fontWeight = FontWeight.Bold)
            }
            HorizontalDivider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRowItem(
    entry: DateEntry,
    onDelete: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(initialHour = 11, initialMinute = 0)
    var expanded by remember { mutableStateOf(false) }

    // Convert selection to String for display --> DD/MM/YYYY
    val dateDisplay = if (entry.day.isNotEmpty()) {
        "${entry.day}/${entry.month}/${entry.year}"
    } else {
        "Select Date"
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis?.let {
                        java.time.Instant.ofEpochMilli(it)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    selectedDate?.let {
                        entry.day = it.dayOfMonth.toString()
                        entry.month = it.monthValue.toString()
                        entry.year = it.year.toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Formats time as "HH:mm"
                    val hour = timePickerState.hour.toString().padStart(2, '0')
                    val min = timePickerState.minute.toString().padStart(2, '0')
                    entry.time = "$hour:$min"
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Column(Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier.weight(3f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Text(
                    text = dateDisplay,
                    modifier = Modifier.padding(16.dp),
                    color = if (entry.day.isEmpty()) Color.Gray else Color.Black
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray)
            }

            // Time Pill
            Surface(onClick = { showTimePicker = true }, shape = RoundedCornerShape(20.dp), color = Color(0xFFF0F0F0)) {
                Text(entry.time, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 14.sp)
            }
        }

        // Repeat Dropdown Row
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Text("Repeat", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.width(8.dp))
            Box {
                OutlinedCard(
                    onClick = { expanded = true },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(entry.repeat, fontSize = 12.sp)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("Never", "Daily", "Weekly").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { entry.repeat = it; expanded = false })
                    }
                }
            }
        }
    }
}