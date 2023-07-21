package com.arvizu.openweather.feature.weather.data.mapper

import com.arvizu.openweather.common.util.extensions.capitalize
import com.arvizu.openweather.common.util.extensions.toFormattedDateString
import com.arvizu.openweather.common.util.extensions.toTimeOfTheDay
import com.arvizu.openweather.feature.weather.data.remote.model.CurrentWeatherApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse
import com.arvizu.openweather.feature.weather.dto.WeatherDTO
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

/*
 * This class is responsible for mapping and transforming the ForecastApiResponse to WeatherUIModel.
 * While this mostly involves simple mapping creating a separate class for this allows us to
 * perform any additional transformations or logic that may be needed in the future, along with
 * isolating the mapping logic from the repository.
 */
@Singleton
class WeatherResponseMapper @Inject constructor() {
    fun mapToDTO(forecastApiResponse: ForecastApiResponse, timeZone: Int, unit: String): Result<WeatherDTO, Exception> {
        // The main object, date and temp should be checked in the moshi adapter however
        // include explicit checks at the data layer in case the adapter is changed.
        forecastApiResponse.main?: missingRequiredField("main")
        forecastApiResponse.dateTime ?: missingRequiredField("dateTime")
        forecastApiResponse.main?.temp ?: missingRequiredField("temp")
        val date = forecastApiResponse.dateTime.toFormattedDateString()
        val temperature = forecastApiResponse.main!!.temp!!
        val humidity = forecastApiResponse.main.humidity
        val windSpeed = forecastApiResponse.wind?.speed
        val clouds = forecastApiResponse.clouds?.all
        val icon = forecastApiResponse.weather?.firstOrNull()?.icon
        val description = forecastApiResponse.weather?.firstOrNull()?.description?.capitalize()
        val timeOfDay = forecastApiResponse.dateTime?.toTimeOfTheDay(timeZone)
        // if icon is not blank or empty construct url
        val iconUrl = if (icon?.isNotBlank() == true) {
            "https://openweathermap.org/img/wn/$icon@4x.png"
        } else {
            null
        }
        return Ok(WeatherDTO(
            date = date,
            temperature = temperature,
            windSpeed = windSpeed,
            humidity = humidity,
            cloudiness = clouds,
            iconUrl = iconUrl,
            description = description,
            timeOfDay = timeOfDay,
            unit = unit
        ))
    }

    fun mapToDTO(currentWeatherApiResponse: CurrentWeatherApiResponse, unit: String): Result<WeatherDTO, Exception> {
        // The main object, date and temp should be checked in the moshi adapter however
        // include explicit checks at the data layer in case the adapter is changed.
        currentWeatherApiResponse.main?: missingRequiredField("main")
        currentWeatherApiResponse.dateTime ?: missingRequiredField("dateTime")
        currentWeatherApiResponse.main?.temp ?: missingRequiredField("temp")
        currentWeatherApiResponse.timezone ?: missingRequiredField("timezone")
        val date = currentWeatherApiResponse.dateTime.toFormattedDateString()
        val temperature = currentWeatherApiResponse.main!!.temp!!
        val humidity = currentWeatherApiResponse.main.humidity
        val windSpeed = currentWeatherApiResponse.wind?.speed
        val clouds = currentWeatherApiResponse.clouds?.all
        val icon = currentWeatherApiResponse.weather?.firstOrNull()?.icon
        val description = currentWeatherApiResponse.weather?.firstOrNull()?.description?.capitalize()
        val timeOfDay = currentWeatherApiResponse.dateTime?.toTimeOfTheDay(currentWeatherApiResponse.timezone!!)
        // if icon is not blank or empty construct url
        val iconUrl = if (icon?.isNotBlank() == true) {
            "https://openweathermap.org/img/wn/$icon@4x.png"
        } else {
            null
        }
        return Ok(WeatherDTO(
            date = date,
            temperature = temperature,
            windSpeed = windSpeed,
            humidity = humidity,
            cloudiness = clouds,
            iconUrl = iconUrl,
            description = description,
            timeOfDay = timeOfDay,
            unit = unit
        ))
    }
    private fun missingRequiredField(field: String): Err<IllegalStateException> {
        return Err(IllegalStateException("Error: weather response field $field is null, check api parser"))
    }
}


