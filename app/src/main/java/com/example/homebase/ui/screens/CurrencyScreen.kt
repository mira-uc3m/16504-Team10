package com.example.homebase.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.homebase.R
import com.example.homebase.data.view.CurrencyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(
    navController: NavHostController,
    viewModel: CurrencyViewModel = viewModel()
) {
    val state = viewModel.state
    var showFromDialog by remember { mutableStateOf(false) }
    var showToDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Currency Conversion",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.currency_exchange_pic),
                    contentDescription = "Currency Conversion Illustration",
                    modifier = Modifier.fillMaxHeight(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Conversion Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // "From" Section
                    Text("From", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BasicTextField(
                            value = state.amount,
                            onValueChange = { viewModel.onAmountChange(it) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            decorationBox = { innerTextField ->
                                if (state.amount.isEmpty()) {
                                    Text("Amount", color = Color.LightGray, fontSize = 16.sp)
                                }
                                innerTextField()
                            }
                        )
                        VerticalDivider(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                            color = Color.LightGray
                        )
                        Row(
                            modifier = Modifier.clickable { showFromDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(state.fromCurrency, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.UnfoldMore, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Swap Icon
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.SwapVert,
                            contentDescription = "Swap",
                            tint = Color(0xFF3022A6),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // "To" Section
                    Text("To", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.convertedAmount != "0.00") state.convertedAmount else "Amount",
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = if (state.convertedAmount != "0.00") Color.Black else Color.LightGray
                        )
                        VerticalDivider(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 12.dp),
                            color = Color.LightGray
                        )
                        Row(
                            modifier = Modifier.clickable { showToDialog = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(state.toCurrency, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.UnfoldMore, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Exchange Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Currency rate",
                            color = Color(0xFF3022A6),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        val currentRate = state.rates[state.toCurrency] ?: 1.0
                        Text(
                            "1 ${state.fromCurrency} = $currentRate ${state.toCurrency}",
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Convert Button
                    Button(
                        onClick = { viewModel.convert() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3022A6)),
                        shape = RoundedCornerShape(16.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Convert", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // From Currency Selection Dialog
    if (showFromDialog) {
        CurrencySelectionDialog(
            title = "Select the currency",
            currentSelection = state.fromCurrency,
            currencies = if (state.rates.isEmpty()) listOf("USD", "EUR", "GBP", "JPY", "CAD") else state.rates.keys.toList(),
            onDismiss = { showFromDialog = false },
            onSelect = {
                viewModel.onFromCurrencyChange(it)
                showFromDialog = false
            }
        )
    }

    // To Currency Selection Dialog
    if (showToDialog) {
        CurrencySelectionDialog(
            title = "Select the currency",
            currentSelection = state.toCurrency,
            currencies = if (state.rates.isEmpty()) listOf("USD", "EUR", "GBP", "JPY", "CAD") else state.rates.keys.toList(),
            onDismiss = { showToDialog = false },
            onSelect = {
                viewModel.onToCurrencyChange(it)
                showToDialog = false
            }
        )
    }
}

@Composable
fun CurrencySelectionDialog(
    title: String,
    currentSelection: String,
    currencies: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(currencies.sorted()) { currency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(currency) }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currency,
                                color = if (currency == currentSelection) Color(0xFF3022A6) else Color.Gray,
                                fontWeight = if (currency == currentSelection) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                            if (currency == currentSelection) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF3022A6),
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
