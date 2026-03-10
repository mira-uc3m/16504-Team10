package com.example.homebase.data.view

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homebase.data.api.CurrencyApi
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CurrencyViewModel : ViewModel() {
    // Setup Retrofit for api requests
    private val api = Retrofit.Builder()
        .baseUrl("https://v6.exchangerate-api.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CurrencyApi::class.java)

    var state by mutableStateOf(CurrencyState())

    init {
        fetchRates("CAD") // Fetch CAD rates on startup
    }
    fun onAmountChange(newAmount: String) {
        state = state.copy(amount = newAmount)
    }

    fun onFromCurrencyChange(newCurrency: String) {
        state = state.copy(fromCurrency = newCurrency)
        fetchRates(newCurrency) // Fetch new rates when base changes
    }

    fun onToCurrencyChange(newCurrency: String) {
        state = state.copy(toCurrency = newCurrency)
    }

    fun fetchRates(base: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null) // Start loading
            try {
                val response = api.getLatestRates(base)
                if (response.isSuccessful) {
                    state = state.copy(
                        rates = response.body()?.conversion_rates ?: emptyMap(),
                        isLoading = false
                    )
                } else {
                    state = state.copy(isLoading = false, errorMessage = "API Error: ${response.code()}")
                }
            } catch (e: Exception) {
                // catches internet timeout, airplane mode, ...
                state = state.copy(isLoading = false, errorMessage = "Check your internet connection")
            }
        }
    }

    fun convert() {
        val rate = state.rates[state.toCurrency] ?: 1.0
        val amt = state.amount.toDoubleOrNull() ?: 0.0
        val result = amt * rate
        state = state.copy(
            convertedAmount = String.format("%.2f", result),
            displayCurrency = state.toCurrency
        )
    }
}

data class CurrencyState(
    val amount: String = "",
    val fromCurrency: String = "CAD",
    val toCurrency: String = "USD",
    val displayCurrency: String = "USD",
    val convertedAmount: String = "0.00",
    val rates: Map<String, Double> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)