package com.example.homebase.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.homebase.R
import com.example.homebase.ui.navigation.Screen
import com.example.homebase.data.view.ScheduleViewModel
import com.example.homebase.data.view.NotificationViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.homebase.data.model.ScheduleEvent
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController, 
    viewModel: ScheduleViewModel,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchScheduleFromFirebase()
    }

    val todayClasses by viewModel.todayClasses
    val notifications by notificationViewModel.notifications.collectAsState()
    val unreadCount = notifications.size

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "HOME BASE",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Notifications.route) }) {
                        if (unreadCount > 0) {
                            BadgedBox(
                                badge = { Badge { Text("$unreadCount") } }
                            ) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_homebase_logo),
                        contentDescription = "Home Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(36.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF3022A6)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(Screen.Home, Screen.Settings)
                navItems.forEach { screen ->
                    val isSelected = screen == Screen.Home
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(screen.route)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color(0xFF3022A6),
                            indicatorColor = Color(0xFF3022A6),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFF3022A6)
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
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Today's Classes",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (todayClasses.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No classes today!",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Enjoy your free time.",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(todayClasses) { event ->
                                RealClassListItem(event)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                val gridItems = listOf(
                    Triple("Currency\nConversion", Icons.Default.AccountBalanceWallet, "currency_screen"),
                    Triple("Campus\nMap", Icons.Default.LocationOn, "map"),
                    Triple("My\nSchedule", Icons.Default.DateRange, "schedule_screen"),
                    Triple("Exchange\nChecklist", Icons.Default.FormatListBulleted, "checklist_screen"),
                    Triple("Class\nList", Icons.Default.ListAlt, "classlist_screen"),
                    Triple("Quick\nLinks", Icons.Default.Link, "links_screen")
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    items(gridItems) { (title, icon, route) ->
                        DashboardCard(title, icon) {
                            navController.navigate(route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RealClassListItem(event: ScheduleEvent) {
    val icons = listOf(
        Icons.Default.Stars,
        Icons.Default.Thunderstorm,
        Icons.Default.Notes,
        Icons.Default.Train,
        Icons.Default.List,
        Icons.Default.School
    )
    val displayIcon = icons.getOrElse(event.iconIndex) { Icons.Default.School }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Background using the color saved in Firebase
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(event.color)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        displayIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    event.name,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF333333)
                )
                Text(
                    event.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                event.time,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3022A6),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF0F0F0))
    }
}

@Composable
fun DashboardCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = when (title) {
                    "Currency\nConversion" -> Color(0xFF2196F3)
                    "Campus\nMap" -> Color(0xFF3022A6)
                    "My\nSchedule" -> Color(0xFFFFB300)
                    "Exchange\nChecklist" -> Color(0xFF4DB6AC)
                    "Class\nList" -> Color(0xFFFF4D4D)
                    "Quick\nLinks" -> Color(0xFF2196F3)
                    else -> Color(0xFF3022A6)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}
