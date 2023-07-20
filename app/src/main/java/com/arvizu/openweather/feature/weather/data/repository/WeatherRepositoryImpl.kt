package com.arvizu.openweather.feature.weather.data.repository

import com.arvizu.openweather.feature.weather.data.datasource.WeatherDataSource
import com.arvizu.openweather.feature.weather.data.mapper.WeatherResponseMapper
import com.arvizu.openweather.feature.weather.presentation.model.WeatherUIModel
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import javax.inject.Inject


class WeatherRepositoryImpl @Inject constructor(
    private val weatherDataSource: WeatherDataSource,
    private val weatherResponseMapper: WeatherResponseMapper
): WeatherRepository {

    override suspend fun getWeather(latLng: Pair<Double, Double>): Result<WeatherUIModel, Exception> {
        return when (val result = weatherDataSource.getWeather(latLng)) {
            is Ok -> {
                val weatherUIModel = weatherResponseMapper.mapToUiModel(result.value)
                Ok(weatherUIModel)
            }

            is Err -> {
                Err(result.error)
            }
        }
    }

}