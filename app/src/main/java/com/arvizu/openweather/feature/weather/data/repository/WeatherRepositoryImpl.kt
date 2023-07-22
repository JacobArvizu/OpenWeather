package com.arvizu.openweather.feature.weather.data.repository

import com.arvizu.openweather.common.util.helpers.SharedPreferencesHelper
import com.arvizu.openweather.feature.weather.data.datasource.WeatherDataSource
import com.arvizu.openweather.feature.weather.data.mapper.WeatherResponseMapper
import com.arvizu.openweather.feature.weather.dto.WeatherDTO
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.time.ZonedDateTime
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherDataSource: WeatherDataSource,
    private val weatherResponseMapper: WeatherResponseMapper,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
): WeatherRepository {
    override suspend fun getCurrentWeather(latLng: Pair<Double, Double>): Result<WeatherDTO, Exception> {
        return when (val result = weatherDataSource.getCurrentWeather(latLng, sharedPreferencesHelper.preferredMeasurementUnit)) {
            is Ok -> {
                when (val weatherDTO = weatherResponseMapper.mapToDTO(result.value, sharedPreferencesHelper.preferredMeasurementUnit)) {
                    is Ok -> Ok(weatherDTO.value)
                    is Err -> weatherDTO
                }
            }
            is Err -> result
        }
    }

    override suspend fun getForecast(latLng: Pair<Double, Double>): Result<List<WeatherDTO>, Exception> {

        return when (val result = weatherDataSource.getForecast(latLng, sharedPreferencesHelper.preferredMeasurementUnit)) {
            is Ok -> {
                val timeZone = result.value.city.timezone ?: ZonedDateTime.now().offset.totalSeconds
                val weatherDTOList = result.value.list.mapNotNull { forecastWeatherData ->
                    when (val weatherDTO = weatherResponseMapper.mapToDTO(forecastWeatherData, timeZone, sharedPreferencesHelper.preferredMeasurementUnit)) {
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