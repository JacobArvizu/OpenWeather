package com.arvizu.openweather.feature.weather.data.repository

import com.arvizu.openweather.common.util.helpers.SharedPreferencesHelper
import com.arvizu.openweather.feature.weather.data.datasource.WeatherDataSource
import com.arvizu.openweather.feature.weather.data.mapper.WeatherResponseMapper
import com.arvizu.openweather.feature.weather.dto.WeatherDTO
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import javax.inject.Inject


class WeatherRepositoryImpl @Inject constructor(
    private val weatherDataSource: WeatherDataSource,
    private val weatherResponseMapper: WeatherResponseMapper,
    private val sharedPreferencesHelper: SharedPreferencesHelper
): WeatherRepository {
    override suspend fun getWeather(latLng: Pair<Double, Double>): Result<List<WeatherDTO>, Exception> {
        return when (val result = weatherDataSource.getWeather(latLng, sharedPreferencesHelper.preferredMeasurementUnit)) {
            is Ok -> {
                val weatherDTOList = result.value.mapNotNull { forecastWeatherData ->
                    when (val weatherDTO = weatherResponseMapper.mapToUiModel(forecastWeatherData)) {
                        is Ok -> weatherDTO.value
                        is Err -> null
                    }
                }
                Ok(weatherDTOList)
            }
            is Err -> result
        }
    }
}