package com.arvizu.openweather.feature.weather.dto

/*
 * Data transfer object for the weather data. Weather data should at the very least
 * contain a date and temperature to be used in the application.
 */
data class WeatherDTO(
    val date: String,
    val temperature: Double,
    val windSpeed: Double?,
    val humidity: Int?,
    val cloudiness: Int?,
    val iconUrl: String?,
    val description: String?
)