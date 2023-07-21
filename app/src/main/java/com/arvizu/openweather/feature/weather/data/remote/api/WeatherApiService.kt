package com.arvizu.openweather.feature.weather.data.remote.api

import com.arvizu.openweather.BuildConfig
import com.arvizu.openweather.feature.weather.data.remote.model.CurrentWeatherApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastListApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/forecast")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY,
    ): Response<ForecastListApiResponse>

    // Use forecast API as it returns current weather as well.
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY,
    ): Response<CurrentWeatherApiResponse>
}
