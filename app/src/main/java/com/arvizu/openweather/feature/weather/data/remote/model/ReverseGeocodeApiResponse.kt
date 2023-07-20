package com.arvizu.openweather.feature.weather.data.remote.model

import com.squareup.moshi.Json

data class ReverseGeocodeApiResponse(
    @Json(name = "name") val name: String?,
    @Json(name = "local_names") val localNames: LocalNames?,
    @Json(name = "lat") val latitude: Double?,
    @Json(name = "lon") val longitude: Double?,
    @Json(name = "country") val country: String?,
    @Json(name = "state") val state: String?
) {
    data class LocalNames(
        @Json(name = "ascii") val ascii: String?,
        @Json(name = "feature_name") val featureName: String?,
        @Json(name = "default") val default: String?,
    )
}