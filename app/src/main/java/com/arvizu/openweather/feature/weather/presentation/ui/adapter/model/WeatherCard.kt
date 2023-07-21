package com.arvizu.openweather.feature.weather.presentation.ui.adapter.model

data class WeatherCard(
    val date: String,
    val temperature: String,
    val windSpeed: String,
    val humidity: String,
    val cloudiness: String,
    val iconUrl: String,
    val description: String,
    val timeOfDay: String
)
