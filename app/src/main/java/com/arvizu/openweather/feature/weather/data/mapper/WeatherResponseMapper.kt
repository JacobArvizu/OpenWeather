package com.arvizu.openweather.feature.weather.data.mapper

import com.arvizu.openweather.common.util.extensions.toFormattedDateString
import com.arvizu.openweather.feature.weather.data.remote.model.ReverseGeocodeApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.WeatherApiResponse
import com.arvizu.openweather.feature.weather.presentation.model.WeatherUIModel
import javax.inject.Inject
import javax.inject.Singleton

/*
 * This class is responsible for mapping and transforming the WeatherApiResponse to WeatherUIModel.
 * While this mostly involves simple mapping creating a separate class for this allows us to
 * perform any additional transformations or logic that may be needed in the future, along with
 * isolating the mapping logic from the repository.
 */
@Singleton
class WeatherResponseMapper @Inject constructor() {

    // For the scope of this project we will allow impartial data and return N/A
    fun mapToUiModel(weatherApiResponse: WeatherApiResponse): WeatherUIModel {
        // Values from the weather API response
        val date = weatherApiResponse.dateTime?.toFormattedDateString() ?: "N/A"
        val temperature = weatherApiResponse.main?.temp?.toString()?.plus("° F") ?: "N/A"
        val humidity = weatherApiResponse.main?.humidity?.toString()?.plus(" %") ?: "N/A"
        val windSpeed = weatherApiResponse.wind?.speed?.toString()?.plus(" mph") ?: "N/A"
        val clouds = weatherApiResponse.clouds?.all?.toString()?.plus(" %") ?: "N/A"
        val icon = weatherApiResponse.weather?.firstOrNull()?.icon ?: ""
        val description = weatherApiResponse.weather?.firstOrNull()?.description ?: "N/A"
        // if icon is not blank or empty construct url
        val iconUrl = if (icon.isNotBlank()) {
            "https://openweathermap.org/img/wn/$icon@4x.png"
        } else {
            ""
        }
        return WeatherUIModel(
            date = date,
            temperature = temperature,
            windSpeed = windSpeed,
            humidity = humidity,
            cloudiness = clouds,
            iconUrl = iconUrl,
            description = description
        )
    }
}


