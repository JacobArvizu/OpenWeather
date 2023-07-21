package com.arvizu.openweather.common.data.repository

interface AppRepository {
    suspend fun setLocationPreferences(latitude: Double, longitude: Double, city: String)
}
