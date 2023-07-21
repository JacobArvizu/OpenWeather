package com.arvizu.openweather.feature.weather.data.datasource

import com.arvizu.openweather.feature.weather.data.remote.model.CurrentWeatherApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastListApiResponse
import com.github.michaelbull.result.Result

interface WeatherDataSource {
    suspend fun getForecast(latLng: Pair<Double, Double>, measurementUnit: String): Result<ForecastListApiResponse, Exception>
    suspend fun getCurrentWeather(latLng: Pair<Double, Double>, measurementUnit: String): Result<CurrentWeatherApiResponse, Exception>
}
