package com.arvizu.openweather.feature.weather.util.exceptions

data class WeatherApiException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause)
