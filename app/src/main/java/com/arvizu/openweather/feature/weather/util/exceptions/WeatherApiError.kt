package com.arvizu.openweather.feature.weather.util.exceptions

sealed class WeatherApiError : Exception() {
    object BadRequest : WeatherApiError()
    object Unauthorized : WeatherApiError()
    object NotFound : WeatherApiError()
    object TooManyRequests : WeatherApiError()
    object InternalServerError : WeatherApiError()
    data class Unknown(val code: Int) : WeatherApiError()
}