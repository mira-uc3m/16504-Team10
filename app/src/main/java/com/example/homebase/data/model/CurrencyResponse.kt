package com.example.homebase.data.model

data class CurrencyResponse(
    val conversion_rates: Map<String, Double>,
    val base_code: String
)