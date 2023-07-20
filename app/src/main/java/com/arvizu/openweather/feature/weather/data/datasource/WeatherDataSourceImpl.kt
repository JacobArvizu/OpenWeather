package com.arvizu.openweather.feature.weather.data.datasource

import com.arvizu.openweather.feature.weather.data.remote.api.WeatherApiService
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse
import com.arvizu.openweather.feature.weather.util.extensions.processResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.squareup.moshi.JsonDataException
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val weatherApiService: WeatherApiService,
): WeatherDataSource {
    override suspend fun getWeather(latLng: Pair<Double, Double>, measurementUnit: String): Result<List<ForecastApiResponse>, Exception> {
        return try {
            val response = weatherApiService.getWeatherData(latLng.first, latLng.second, measurementUnit)
            response.processResponse { data ->
                Ok(data.list)
            }
        } catch (e: JsonDataException) {
            Err(e)
        }
    }

}