package com.example.homebase.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
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
                    IconButton(onClick = { /* Handle Notifications */ }) {
                        BadgedBox(
                            badge = { 
                                Badge(
                                    containerColor = Color(0xFFFF4D4D),
                                    contentColor = Color.White
                                ) { 
                                    Text("3") 
                                } 
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    Surface(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(40.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_homebase_logo),
                            contentDescription = "Home Base Logo",
                            modifier = Modifier.padding(4.dp)
                        )
                    }
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
        // Main content area with rounded top corners
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
                // Today's Classes Section (Scrollable)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Today's Classes",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Demo data
                        items(5) { index ->
                            ClassListItem(index)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6 Buttons Section (Fixed at bottom)
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
fun ClassListItem(index: Int) {
    val titles = listOf("Mobile Applications", "Class 2", "Class 3", "Class 4", "Class 5")
    val rooms = listOf("4.0.G01", "1.0.D01", "4.0.D04", "7.0.D06", "1.0.D01")
    val times = listOf("9:00 h", "11:00 h", "13:00 h", "15:00 h", "17:00 h")
    val colors = listOf(Color(0xFF3022A6), Color(0xFFFF4D4D), Color(0xFF2196F3), Color(0xFFFFB300), Color(0xFF4DB6AC))
    val icons = listOf(Icons.Default.WaterDrop, Icons.Default.ConfirmationNumber, Icons.Default.PushPin, Icons.Default.Dashboard, Icons.Default.CalendarToday)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors[index % colors.size]
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icons[index % icons.size], contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    titles[index % titles.size],
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF333333)
                )
                Text(
                    rooms[index % rooms.size],
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                times[index % times.size],
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
