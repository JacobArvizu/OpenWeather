package com.arvizu.openweather.feature.weather.data.repository

import com.arvizu.openweather.feature.weather.dto.WeatherDTO
import com.github.michaelbull.result.Result

interface WeatherRepository {
    suspend fun getWeather(latLng: Pair<Double, Double>): Result<List<WeatherDTO>, Exception>
}
