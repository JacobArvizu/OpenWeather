package com.arvizu.openweather.feature.weather.data.datasource

import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse
import com.github.michaelbull.result.Result

interface WeatherDataSource {
    suspend fun getWeather(latLng: Pair<Double, Double>, measurementUnit: String): Result<List<ForecastApiResponse>, Exception>
}
