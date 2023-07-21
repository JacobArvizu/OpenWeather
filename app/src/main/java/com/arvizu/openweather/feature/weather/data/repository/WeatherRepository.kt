package com.arvizu.openweather.feature.weather.data.repository

import com.arvizu.openweather.feature.weather.dto.WeatherDTO
import com.github.michaelbull.result.Result

interface WeatherRepository {
    suspend fun getCurrentWeather(latLng: Pair<Double, Double>): Result<WeatherDTO, Exception>
    suspend fun getForecast(latLng: Pair<Double, Double>): Result<List<WeatherDTO>, Exception>
}
