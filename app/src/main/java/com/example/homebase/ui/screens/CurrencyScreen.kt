package com.example.homebase.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.homebase.data.view.CurrencyViewModel
import androidx.compose.material3.CircularProgressIndicator
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel = viewModel()) {
    val state = viewModel.state
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Currency Converter", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // Amount Input
        OutlinedTextField(
            value = state.amount,
            onValueChange = { viewModel.onAmountChange(it) },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        // FROM Currency Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedFrom,
            onExpandedChange = { expandedFrom = !expandedFrom }
        ) {
            OutlinedTextField(
                value = state.fromCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("From") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                // List of common currencies or state.rates.keys
                listOf("CAD", "USD", "EUR", "GBP", "JPY").forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            viewModel.onFromCurrencyChange(currency)
                            expandedFrom = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TO Currency Dropdown
        ExposedDropdownMenuBox(
            expanded = expandedTo,
            onExpandedChange = { expandedTo = !expandedTo }
        ) {
            OutlinedTextField(
                value = state.toCurrency,
                onValueChange = {},
                readOnly = true,
                label = { Text("To") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                state.rates.keys.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            viewModel.onToCurrencyChange(currency)
                            expandedTo = false
                        }
                    )
                }
            }
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.convert() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Convert")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Result", style = MaterialTheme.typography.labelLarge)
                Text("${state.convertedAmount} ${state.displayCurrency}",
                    style = MaterialTheme.typography.displaySmall)
            }
        }
    }
}