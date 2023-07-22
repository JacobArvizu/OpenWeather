package com.arvizu.openweather.common.use_case

import com.arvizu.openweather.common.data.model.LocationEntity
import com.arvizu.openweather.common.data.repository.AppRepository
import com.github.michaelbull.result.Result
import javax.inject.Inject

class GetLocationPreferencesUseCase @Inject constructor(
    private val appRepository: AppRepository
) {
    suspend fun execute(): Result<LocationEntity, Exception> {
        return appRepository.getLocationPreferences()
    }
}