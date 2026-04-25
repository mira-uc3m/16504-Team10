package com.example.homebase.data.model

data class DateEntry(
    var day: String = "",
    var month: String = "",
    var year: String = "",
    var time: String = "11:00",
    var repeat: String = "Never",
    var endDay: String = "",
    var endMonth: String = "",
    var endYear: String = "",
    var isIndefinite: Boolean = true
)