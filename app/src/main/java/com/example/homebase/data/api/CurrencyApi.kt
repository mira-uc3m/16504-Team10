package com.example.homebase.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.homebase.data.model.CurrencyResponse
import com.example.homebase.BuildConfig
import com.google.gson.internal.GsonBuildConfig

interface CurrencyApi {
    // Replace "YOUR_KEY" with a free key from exchangerate-api.com
    @GET("v6/${BuildConfig.CURRENCY_API_KEY}/latest/{base}")
    suspend fun getLatestRates(@Path("base") base: String): Response<CurrencyResponse>
}