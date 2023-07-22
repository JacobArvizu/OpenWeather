package com.arvizu.openweather.common.data.repository

import com.arvizu.openweather.common.data.model.LocationEntity
import com.github.michaelbull.result.Result

interface AppRepository {
    suspend fun setLocationPreferences(latitude: Double, longitude: Double, city: String)
    suspend fun getLocationPreferences(): Result<LocationEntity, Exception>
}
