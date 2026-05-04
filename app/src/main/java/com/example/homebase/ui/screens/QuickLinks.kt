package com.example.homebase.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickLinksScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf("Before you Go") }
    val tabs = listOf("Before you Go", "Housing", "City Life")
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Quick Links",
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
        containerColor = Color(0xFF3022A6)
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize(),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tab },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF3022A6) else Color(0xFFF0F0F0),
                        ) {
                            Text(
                                text = tab,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                color = if (isSelected) Color.White else Color.DarkGray,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTab) {
                        "Before you Go" -> {
                            QuickLinkCard(
                                title = "Consular Website",
                                description = "Up to date information for all Visa needs",
                                illustration = { ConsularIllustration() },
                                onClick = { uriHandler.openUri("https://www.exteriores.gob.es/Consulados/toronto/en/ServiciosConsulares/Paginas/Consular/Visado-de-estudios.aspx") }
                            )
                            QuickLinkCard(
                                title = "Medical Certificate",
                                description = "Form to be filled out by doctor with Spanish translation included",
                                illustration = { MedicalIllustration() },
                                onClick = { uriHandler.openUri("https://www.exteriores.gob.es/DocumentosAuxiliaresSC/Estados%20Unidos/LOS%20%C3%81NGELES%20%28C%29/Certificado%20Medico.pdf") }
                            )
                            QuickLinkCard(
                                title = "Phone Plans",
                                description = "Mobile Phone plan options to fit all your traveling needs",
                                illustration = { PhonePlansIllustration() },
                                onClick = { uriHandler.openUri("https://travel.vodafone.com/?c_code=sea-131-ao-VG_25_AO_P_C_J_E_X_EU-SPAIN-ALL-NB&gad_source=1&gad_campaignid=22689075521&gbraid=0AAAAAqPxYcEjWoxR1oizVhIQFRd39a9QU&gclid=CjwKCAjwqazPBhALEiwAOuXqdCPLubFbB0xOOMas2QqMmjHm9-KNPJE3SlFTkMcoDHso1sj58BWh9BoCZ2AQAvD_BwE") }
                            )
                            QuickLinkCard(
                                title = "Amazon Essentials",
                                description = "Amazon must-haves to purchase before your travels",
                                illustration = { AmazonIllustration() },
                                onClick = { uriHandler.openUri("https://www.amazon.com/study-abroad-essentials/s?k=study+abroad+essentials") }
                            )
                        }
                        "Housing" -> {
                            QuickLinkCard(
                                title = "Airbnb",
                                description = "Find vacation rentals, cabins, beach houses and more",
                                illustration = { HousingIllustration() },
                                onClick = { uriHandler.openUri("https://www.airbnb.com/") }
                            )
                        }
                        "City Life" -> {
                            QuickLinkCard(
                                title = "City Life Madrid",
                                description = "The go-to community for international people living in Madrid",
                                illustration = { CityLifeIllustration() },
                                onClick = { uriHandler.openUri("https://www.citylifemadrid.com/") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun QuickLinkCard(
    title: String,
    description: String,
    illustration: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF8F9FA),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier.size(width = 100.dp, height = 90.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                illustration()
            }
        }
    }
}

@Composable
fun PhoneFrame(
    modifier: Modifier = Modifier,
    screenContent: @Composable BoxScope.() -> Unit
) {
    // Ground Shadow
    Surface(
        modifier = Modifier
            .padding(bottom = 2.dp)
            .size(width = 60.dp, height = 8.dp),
        color = Color.Black.copy(alpha = 0.05f),
        shape = CircleShape
    ) {}
    
    // Phone Body
    Surface(
        modifier = modifier
            .size(width = 44.dp, height = 76.dp),
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF3022A6)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Notch
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 2.dp)
                    .size(width = 12.dp, height = 2.dp),
                color = Color(0xFF3022A6),
                shape = RoundedCornerShape(1.dp)
            ) {}
            
            screenContent()
            
            // Bottom "Action" button area
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp)
                    .size(width = 24.dp, height = 6.dp),
                color = Color(0xFFFFB300).copy(alpha = 0.8f),
                shape = RoundedCornerShape(2.dp)
            ) {}
        }
    }
}

@Composable
fun ConsularIllustration() {
    PhoneFrame {
        // Orange Paper/Scroll
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = 28.dp, height = 40.dp),
            color = Color(0xFFFDBB2D),
            shape = RoundedCornerShape(2.dp)
        ) {
            Column(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(6) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.5f)))
                }
            }
        }
    }
}

@Composable
fun MedicalIllustration() {
    Box(contentAlignment = Alignment.BottomCenter) {
        PhoneFrame {
            // Red Cross
            Box(modifier = Modifier.align(Alignment.Center).size(16.dp)) {
                Box(modifier = Modifier.align(Alignment.Center).fillMaxWidth().height(4.dp).background(Color(0xFFFF4D4D)))
                Box(modifier = Modifier.align(Alignment.Center).fillMaxHeight().width(4.dp).background(Color(0xFFFF4D4D)))
            }
        }
        // Small blue drop
        Surface(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 12.dp, bottom = 12.dp).size(16.dp),
            color = Color(0xFF3022A6),
            shape = CircleShape
        ) { Icon(Icons.Default.WaterDrop, null, tint = Color.White, modifier = Modifier.padding(2.dp)) }
        // Small gold coin
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 10.dp).size(18.dp),
            color = Color(0xFFFFB300),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
        ) { Box(contentAlignment = Alignment.Center) { Text("$", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) } }
    }
}

@Composable
fun PhonePlansIllustration() {
    Box(contentAlignment = Alignment.BottomCenter) {
        // Main Phone
        PhoneFrame {
            Box(modifier = Modifier.fillMaxSize().padding(4.dp).background(Color(0xFFFF4D4D).copy(alpha = 0.1f), RoundedCornerShape(2.dp)))
        }
        // Second smaller phone
        Surface(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 2.dp, bottom = 4.dp).size(width = 24.dp, height = 44.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF3022A6)),
            shape = RoundedCornerShape(4.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CreditCard, null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
            }
        }
    }
}

@Composable
fun AmazonIllustration() {
    Box(contentAlignment = Alignment.BottomCenter) {
        PhoneFrame {
            // Red internal list
            Surface(
                modifier = Modifier.align(Alignment.Center).size(width = 24.dp, height = 36.dp),
                color = Color(0xFFFF4D4D).copy(alpha = 0.8f),
                shape = RoundedCornerShape(2.dp)
            ) {}
        }
        // Small card with coin
        Surface(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp, bottom = 14.dp).size(width = 36.dp, height = 24.dp),
            color = Color(0xFF3022A6),
            shape = RoundedCornerShape(4.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(modifier = Modifier.size(12.dp), color = Color(0xFFFFB300), shape = CircleShape) {}
            }
        }
    }
}

@Composable
fun HousingIllustration() {
    PhoneFrame {
        Icon(
            Icons.Default.Home,
            contentDescription = null,
            tint = Color(0xFFFF4D4D),
            modifier = Modifier.align(Alignment.Center).size(32.dp)
        )
    }
}

@Composable
fun CityLifeIllustration() {
    PhoneFrame {
        Icon(
            Icons.Default.LocationCity,
            contentDescription = null,
            tint = Color(0xFF2196F3),
            modifier = Modifier.align(Alignment.Center).size(32.dp)
        )
    }
}
