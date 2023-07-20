package com.arvizu.openweather.feature.weather.use_case

import com.arvizu.openweather.feature.weather.data.repository.WeatherRepository
import com.arvizu.openweather.feature.weather.dto.WeatherDTO
import com.github.michaelbull.result.Result
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend fun execute(latLng: Pair<Double, Double>): Result<List<WeatherDTO>, Exception> {
        return repository.getWeather(latLng)
    }
}