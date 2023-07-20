package com.arvizu.openweather.feature.weather.di

import com.arvizu.openweather.common.util.constants.NetworkConstants
import com.arvizu.openweather.feature.weather.data.mapper.WeatherResponseMapper
import com.arvizu.openweather.feature.weather.data.remote.api.WeatherApiService
import com.arvizu.openweather.feature.weather.util.constants.WeatherNetworkConstants
import com.arvizu.openweather.feature.weather.util.qualifiers.WeatherApi
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

/**
 * This Hilt module is part of the Network layer in the MVVM architecture as such it is scoped
 * as a Singleton as we only need one instance of the dependencies provided here.
 * It provides the dependencies required for making network API requests to the weather API.
 */
@InstallIn(SingletonComponent::class)
@Module
object WeatherApiModule {

    @Provides
    @Singleton
    @WeatherApi
    fun provideWeatherRetrofit(moshi: Moshi, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WeatherNetworkConstants.WEATHER_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(@WeatherApi retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherResponseMapper(): WeatherResponseMapper {
        return WeatherResponseMapper()
    }
}