package com.arvizu.openweather.feature.weather.data.repository

import com.arvizu.openweather.feature.weather.presentation.model.WeatherUIModel
import com.github.michaelbull.result.Result

interface WeatherRepository {
    suspend fun getWeather(latLng: Pair<Double, Double>): Result<WeatherUIModel, Exception>
}
