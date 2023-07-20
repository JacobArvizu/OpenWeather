package com.arvizu.openweather.feature.weather.di

import com.arvizu.openweather.feature.weather.data.datasource.WeatherDataSource
import com.arvizu.openweather.feature.weather.data.datasource.WeatherDataSourceImpl
import com.arvizu.openweather.feature.weather.data.mapper.WeatherResponseMapper
import com.arvizu.openweather.feature.weather.data.remote.api.WeatherApiService
import com.arvizu.openweather.feature.weather.data.repository.WeatherRepository
import com.arvizu.openweather.feature.weather.data.repository.WeatherRepositoryImpl
import com.arvizu.openweather.feature.weather.use_case.GetWeatherUseCase
import com.arvizu.openweather.feature.weather.use_case.WeatherUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * This Hilt module is a part of the Data & Domain layer for business logic in the MVVM architecture.
 * It provides dependencies for accessing and manipulating weather data.
 *
 * This module is scoped to a single ViewModel's lifecycle, meaning they will be created once per ViewModel
 * and will be destroyed when the ViewModel is destroyed. This ensures that the classes only live
 * as part of the most minimal scope necessary.
 */
@InstallIn(ViewModelComponent::class)
@Module
object WeatherModule {

    /**
     * Provides the implementation of [WeatherDataSource], which is a part of the Data layer in MVVM.
     * It's responsible for fetching weather data from the Network layer (WeatherApiService).
     */
    @Provides
    @ViewModelScoped
    fun provideWeatherDataSource(weatherApiService: WeatherApiService): WeatherDataSource {
        return WeatherDataSourceImpl(weatherApiService)
    }

    /**
     * Provides the implementation of [WeatherRepository], a part of the Data layer in MVVM.
     * It's the single source of truth for all weather-related data and is used by the Domain layer (UseCases).
     */
    @Provides
    @ViewModelScoped
    fun provideWeatherRepository(weatherDataSource: WeatherDataSource, weatherResponseMapper: WeatherResponseMapper): WeatherRepository {
        return WeatherRepositoryImpl(weatherDataSource, weatherResponseMapper)
    }

    /**
     * Provides the implementation of [WeatherUseCases], which is part of the Domain layer in MVVM.
     * It represents a collection of use cases related to weather data, used by the ViewModel layer.
     */
    @Provides
    @ViewModelScoped
    fun provideWeatherUseCases(weatherRepository: WeatherRepository): WeatherUseCases {
        return WeatherUseCases(
            GetWeatherUseCase(weatherRepository)
        )
    }
}