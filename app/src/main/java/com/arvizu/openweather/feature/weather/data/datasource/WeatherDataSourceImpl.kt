package com.arvizu.openweather.feature.weather.data.datasource

import com.arvizu.openweather.feature.weather.data.remote.api.WeatherApiService
import com.arvizu.openweather.feature.weather.data.remote.model.WeatherApiResponse
import com.arvizu.openweather.feature.weather.util.extensions.processResponse
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
): WeatherDataSource {

    override suspend fun getWeather(latLng: Pair<Double, Double>): Result<WeatherApiResponse, Exception> {
        return weatherApiService.getWeatherData(latLng.first, latLng.second).processResponse { weatherData ->
            return@processResponse Ok(weatherData)
        }
    }
}