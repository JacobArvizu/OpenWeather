package com.arvizu.openweather.feature.weather.presentation.model

data class WeatherUIModel(
    val date: String,
    val temperature: String,
    val windSpeed: String,
    val humidity: String,
    val cloudiness: String,
    val iconUrl: String,
    val description: String
)
