package com.arvizu.openweather.common.data.repository

import androidx.datastore.core.DataStore
import com.arvizu.openweather.common.data.model.AppSettings
import com.arvizu.openweather.common.data.model.LocationEntity
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<AppSettings>
) : AppRepository {
    override suspend fun setLocationPreferences(latitude: Double, longitude: Double, city: String) {
        dataStore.updateData {
            it.copy(
                locationEntity = LocationEntity(
                    latitude = latitude,
                    longitude = longitude,
                    city = city
                )
            )
        }
    }
    override suspend fun getLocationPreferences(): Result<LocationEntity, Exception> {
        return try {
            val appSettings = dataStore.data.first()
            appSettings.locationEntity?.let {
                Ok(it)
            } ?: Err(NullPointerException("LocationEntity in AppSettings is null"))
        } catch (e: Exception) {
            Err(e)
        }
    }
}