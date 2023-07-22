package com.arvizu.openweather.common.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val locationEntity: LocationEntity? = null
)
@Serializable
data class LocationEntity(
    val latitude: Double,
    val longitude: Double,
    val city: String
)