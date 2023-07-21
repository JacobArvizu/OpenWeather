package com.arvizu.openweather.feature.weather.use_case

data class WeatherUseCases(
    val getForecast: GetForecastUseCase,
    val getCurrentWeather: GetCurrentWeatherUseCase
)
