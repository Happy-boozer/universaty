package com.example.myapplication323656


data class WeatherData(
    val city: String,
    val temperature: Double
)

data class ReportResult(
    val citiesData: List<WeatherData>,
    val averageTemperature: Double
)