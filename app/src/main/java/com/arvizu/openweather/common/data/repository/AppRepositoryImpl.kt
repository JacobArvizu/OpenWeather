package com.arvizu.openweather.common.data.repository

import androidx.datastore.core.DataStore
import com.arvizu.openweather.common.data.model.AppSettings
import com.arvizu.openweather.common.data.model.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<AppSettings>
) : AppRepository {
    override suspend fun setLocationPreferences(latitude: Double, longitude: Double, city: String) {
        dataStore.updateData {
            it.copy(
                location = Location(
                    latitude = latitude,
                    longitude = longitude,
                    city = city
                )
            )
        }
    }
}