package com.arvizu.openweather.common.use_case

import com.arvizu.openweather.common.data.repository.AppRepository
import javax.inject.Inject

class SetLocationPreferencesUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(latitude: Double, longitude: Double, city: String) {
        appRepository.setLocationPreferences(latitude, longitude, city)
    }
}
