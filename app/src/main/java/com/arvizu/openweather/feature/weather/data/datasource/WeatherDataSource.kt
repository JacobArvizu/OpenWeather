package com.arvizu.openweather.feature.weather.data.datasource

import com.arvizu.openweather.feature.weather.data.remote.model.WeatherApiResponse
import com.github.michaelbull.result.Result

interface WeatherDataSource {
    suspend fun getWeather(latLng: Pair<Double, Double>): Result<WeatherApiResponse, Exception>
}
