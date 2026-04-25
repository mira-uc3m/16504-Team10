package com.example.homebase.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homebase.R
import com.example.homebase.data.view.ChecklistViewModel
import com.example.homebase.data.view.ChecklistItem
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistScreen(
    navController: NavHostController,
    viewModel: ChecklistViewModel = viewModel()
) {
    val items = viewModel.items
    val progress = viewModel.getProgress()
    val animatedProgress by animateFloatAsState(targetValue = progress)
    
    var selectedTerm by remember { mutableStateOf("Fall Term") }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Checklist",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3022A6)
                )
            )
        },
        containerColor = Color(0xFF3022A6),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF3022A6),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, "Add") },
                text = { Text("Add to Checklist") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TermTab(
                        text = "Fall Term",
                        isSelected = selectedTerm == "Fall Term",
                        onClick = { 
                            selectedTerm = "Fall Term"
                            viewModel.loadFallTerm()
                        },
                        modifier = Modifier.weight(1f)
                    )
                    TermTab(
                        text = "Winter Term",
                        isSelected = selectedTerm == "Winter Term",
                        onClick = { 
                            selectedTerm = "Winter Term"
                            viewModel.loadWinterTerm()
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            "Your Progress",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF333333)
                        )
                        Text(
                            "${(progress * 100).toInt()}%",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF3022A6)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp),
                        color = Color(0xFF3022A6),
                        trackColor = Color(0xFFE9EEF0),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    val grouped = items.groupBy { it.category }
                    
                    grouped.forEach { (category, categoryItems) ->
                        item {
                            Text(
                                text = category,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        items(categoryItems) { item ->
                            ExpandableChecklistCard(
                                item = item,
                                onToggle = { id, parentId -> viewModel.toggleItem(id, parentId) },
                                onSetReminder = { id, calendar -> viewModel.setReminder(id, calendar) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddChecklistItemDialog(
            existingItems = items,
            onDismiss = { showAddDialog = false },
            onAdd = { title, category, parentId ->
                if (parentId == null) {
                    viewModel.addItem(title, category)
                } else {
                    viewModel.addSubItem(parentId, title)
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ExpandableChecklistCard(
    item: ChecklistItem,
    onToggle: (Int, Int?) -> Unit,
    onSetReminder: (Int, Calendar) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasSubItems = item.subItems.isNotEmpty()
    val context = LocalContext.current

    val primaryBlue = Color(0xFF3022A6)

    val showTimePicker: (Int, Calendar) -> Unit = { id, calendar ->
        val timePickerDialog = TimePickerDialog(
            context,
            R.style.CustomPickerTheme,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)
                onSetReminder(id, calendar)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    val showDatePicker: (Int) -> Unit = { id ->
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            R.style.CustomPickerTheme,
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                showTimePicker(id, calendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (hasSubItems) isExpanded = !isExpanded else onToggle(item.id, null) },
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF8F9FA),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                if (item.isDone) primaryBlue.copy(alpha = 0.5f) else Color.Transparent
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isDone,
                    onCheckedChange = { onToggle(item.id, null) },
                    colors = CheckboxDefaults.colors(checkedColor = primaryBlue)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (item.isDone) Color.Gray else Color(0xFF333333),
                    fontWeight = if (item.isDone) FontWeight.Normal else FontWeight.Medium
                )
                
                IconButton(onClick = { showDatePicker(item.id) }) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Set Reminder",
                        tint = if (item.reminderTime != null) primaryBlue else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                if (hasSubItems) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                } else if (item.hasDetailArrow) {
                    Icon(
                        Icons.Default.ArrowRight,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, top = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item.subItems.forEach { subItem ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(subItem.id, item.id) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F4F5)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = subItem.isDone,
                                onCheckedChange = { onToggle(subItem.id, item.id) },
                                colors = CheckboxDefaults.colors(checkedColor = primaryBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subItem.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (subItem.isDone) Color.Gray else Color(0xFF333333)
                            )
                            IconButton(onClick = { showDatePicker(subItem.id) }) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Set Reminder",
                                    tint = if (subItem.reminderTime != null) primaryBlue else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddChecklistItemDialog(
    existingItems: List<ChecklistItem>,
    onDismiss: () -> Unit,
    onAdd: (String, String, Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("This Week") }
    var selectedParentId by remember { mutableStateOf<Int?>(null) }
    var isSubItemMode by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (isSubItemMode) "Add Step to Task" else "Add New Task",
                    fontWeight = FontWeight.Bold, 
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Mode Selection
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !isSubItemMode,
                        onClick = { isSubItemMode = false },
                        label = { Text("New Main Task") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF3022A6),
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = isSubItemMode,
                        onClick = { isSubItemMode = true },
                        label = { Text("Add to Existing") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF3022A6),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                if (isSubItemMode) {
                    Text("Select Main Task", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Gray)
                    var expanded by remember { mutableStateOf(false) }
                    val selectedParent = existingItems.find { it.id == selectedParentId }
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = { expanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(selectedParent?.title ?: "Select a task...")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            existingItems.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.title) },
                                    onClick = {
                                        selectedParentId = item.id
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isSubItemMode) "Step Name" else "Task Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF3022A6),
                        focusedLabelColor = Color(0xFF3022A6)
                    )
                )
                
                if (!isSubItemMode) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Category", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Gray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val categories = listOf("This Week", "Upcoming")
                        categories.forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF3022A6),
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
                    Button(
                        onClick = { 
                            if (title.isNotBlank()) {
                                onAdd(title, selectedCategory, if (isSubItemMode) selectedParentId else null)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3022A6)),
                        enabled = title.isNotBlank() && (!isSubItemMode || selectedParentId != null)
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
fun TermTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF3022A6) else Color(0xFFF0F0F0)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = TextAlign.Center,
            color = if (isSelected) Color.White else Color.DarkGray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
