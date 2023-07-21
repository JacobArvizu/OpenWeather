package com.arvizu.openweather.feature.weather.data.remote.model

import com.squareup.moshi.Json

data class ForecastListApiResponse(
    @Json(name = "list")
    val list: List<ForecastApiResponse>,
    @Json(name = "city")
    val city: City
) {
    data class City(
        @Json(name = "coord")
        val coord: Coord?,
        @Json(name = "country")
        val country: String?,
        @Json(name = "id")
        val id: Int?,
        @Json(name = "name")
        val name: String?,
        @Json(name = "population")
        val population: Int?,
        @Json(name = "sunrise")
        val sunrise: Int?,
        @Json(name = "sunset")
        val sunset: Int?,
        @Json(name = "timezone")
        val timezone: Int?
    ) {
        data class Coord(
            @Json(name = "lat")
            val lat: Double?,
            @Json(name = "lon")
            val lon: Double?
        )
    }
}