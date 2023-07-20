package com.arvizu.openweather.feature.weather.data.remote.api

import com.arvizu.openweather.common.util.ListApiResponseWrapper
import com.arvizu.openweather.common.util.constants.NetworkConstants
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/forecast")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("appid") apiKey: String = NetworkConstants.OPEN_WEATHER_API_KEY,
    ): Response<ListApiResponseWrapper<ForecastApiResponse>>

    // Use forecast API as it returns current weather as well.
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = NetworkConstants.OPEN_WEATHER_API_KEY,
    ): Response<ForecastApiResponse>
}
