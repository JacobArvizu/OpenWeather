package com.arvizu.openweather.feature.weather.data.remote.api

import com.arvizu.openweather.common.util.constants.NetworkConstants
import com.arvizu.openweather.feature.weather.data.remote.model.WeatherApiResponse
import com.arvizu.openweather.feature.weather.util.constants.WeatherNetworkConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("data/2.5/weather")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = NetworkConstants.OPEN_WEATHER_API_KEY,
    ): Response<WeatherApiResponse>
}
