package com.arvizu.openweather.common.data.model

import com.arvizu.openweather.common.util.constants.AppConstants
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val units: String = AppConstants.MEASUREMENT_UNIT_IMPERIAL,
    val location: Location? = null
)
@Serializable
data class Location(
    val latitude: Double,
    val longitude: Double,
    val city: String,
    )