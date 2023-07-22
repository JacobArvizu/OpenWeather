package com.arvizu.openweather.feature.weather.data.repository

import com.arvizu.openweather.common.util.helpers.SharedPreferencesHelper
import com.arvizu.openweather.feature.weather.data.datasource.WeatherDataSource
import com.arvizu.openweather.feature.weather.data.mapper.WeatherResponseMapper
import com.arvizu.openweather.feature.weather.data.remote.model.CurrentWeatherApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse
import com.arvizu.openweather.feature.weather.dto.WeatherDTO
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/*
 * This class is responsible for mapping and transforming the ForecastApiResponse to WeatherDTOModel.
 * The important test on this layer are that while the response may return true, we do not have much
 * control over the data that is returned or the API. So we must ensure that the response is not only valid
 * but that the data is valid in the format we expect. We are using Mockk here as it allows
 * us to mockk the mapper classes better than Mockito without an interface.
 */
class WeatherRepositoryImplTest {

    private lateinit var weatherDataSource: WeatherDataSource
    private lateinit var weatherResponseMapper: WeatherResponseMapper
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var weatherRepository: WeatherRepositoryImpl

    private val latLng = Pair(1.0, 1.0)
    private val measurementUnit = "metric"

    private val validCurrentWeatherApiResponse = CurrentWeatherApiResponse(
        base = "stations",
        clouds = CurrentWeatherApiResponse.Clouds(all = 20),
        responseCode = 200,
        coordinates = CurrentWeatherApiResponse.Coord(latitude = 35.6895, longitude = 139.6917),
        dateTime = 1560358331,
        id = 1851632,
        main = CurrentWeatherApiResponse.Main(
            feelsLike = 270.15,
            humidity = 81,
            pressure = 1016,
            temp = 281.52,
            tempMax = 283.71,
            tempMin = 279.82
        ),
        name = "Tokyo",
        sys = CurrentWeatherApiResponse.Sys(
            country = "JP",
            id = 8074,
            sunrise = 1560292963,
            sunset = 1560346453,
            type = 1
        ),
        timezone = 32400,
        visibility = 10000,
        weather = listOf(
            CurrentWeatherApiResponse.Weather(
                description = "clear sky",
                icon = "01d",
                id = 800,
                main = "Clear"
            )
        ),
        wind = CurrentWeatherApiResponse.Wind(deg = 80, gust = 8.2, speed = 6.7)
    )

    private val invalidCurrentWeatherApiResponse = CurrentWeatherApiResponse(
        base = "",
        clouds = null,
        responseCode = 200,
        coordinates = null,
        dateTime = null,
        id = null,
        main = null,
        name = "",
        sys = null,
        timezone = null,
        visibility = null,
        weather = null,
        wind = null
    )

    private val validForecastApiResponse = ForecastApiResponse(
        base = "stations",
        clouds = ForecastApiResponse.Clouds(
            all = 1
        ),
        responseCode = 200,
        coordinates = ForecastApiResponse.Coord(
            latitude = 34.0522,
            longitude = -118.2437
        ),
        dateTime = 1625500000,
        id = 1,
        main = ForecastApiResponse.Main(
            feelsLike = 35.0,
            humidity = 30,
            pressure = 1013,
            temp = 36.7,
            tempMax = 38.0,
            tempMin = 34.5
        ),
        name = "Los Angeles",
        sys = ForecastApiResponse.Sys(
            country = "US",
            id = 1,
            sunrise = 1625500000,
            sunset = 1625550000,
            type = 1
        ),
        timezone = -25200,
        visibility = 10000,
        weather = listOf(
            ForecastApiResponse.Weather(
                description = "clear sky",
                icon = "01d",
                id = 1,
                main = "Clear"
            )
        ),
        wind = ForecastApiResponse.Wind(
            deg = 320,
            gust = 11.3,
            speed = 7.2
        )
    )

    private val invalidForecastApiResponse = ForecastApiResponse(
        base = "",
        clouds = null,
        responseCode = 200,
        coordinates = null,
        dateTime = null,
        id = null,
        main = null,
        name = "",
        sys = null,
        timezone = null,
        visibility = null,
        weather = null,
        wind = null
    )

    val weatherDTO = WeatherDTO(
        date = "",
        temperature = 0.0,
        windSpeed = null,
        humidity = null,
        cloudiness = null,
        iconUrl = null,
        description = null,
        timeOfDay = null,
        unit = ""
    )

    @BeforeEach
    fun setup() {
        weatherDataSource = mockk()
        weatherResponseMapper = mockk()
        sharedPreferencesHelper = mockk()

        coEvery { sharedPreferencesHelper.preferredMeasurementUnit } returns measurementUnit

        weatherRepository = WeatherRepositoryImpl(
            weatherDataSource, weatherResponseMapper, sharedPreferencesHelper
        )
    }

    @Test
    fun `getCurrentWeather returns weather data if response and data are valid`() = runBlockingTest {
        coEvery { weatherDataSource.getCurrentWeather(latLng, measurementUnit) } returns Ok(validCurrentWeatherApiResponse)
        coEvery { weatherResponseMapper.mapToDTO(validCurrentWeatherApiResponse, measurementUnit) } returns Ok(weatherDTO)

        val result = weatherRepository.getCurrentWeather(latLng)

        assertTrue(result is Ok)
        assertEquals(weatherDTO, (result as Ok).value)
    }

    @Test
    fun `getCurrentWeather returns error if response is successful but data is invalid`() = runBlockingTest {
        coEvery { weatherDataSource.getCurrentWeather(latLng, measurementUnit) } returns Ok(invalidCurrentWeatherApiResponse)
        coEvery { weatherResponseMapper.mapToDTO(invalidCurrentWeatherApiResponse, measurementUnit) } returns Err(Exception("Invalid data"))

        val result = weatherRepository.getCurrentWeather(latLng)

        assertTrue(result is Err)
    }

    @Test
    fun `getForecast returns error if dataSource returns an error`() = runBlockingTest {
        val dataSourceException = Exception("Data source error")
        coEvery { weatherDataSource.getForecast(latLng, measurementUnit) } returns Err(dataSourceException)

        val result = weatherRepository.getForecast(latLng)

        assertTrue(result is Err)
        assertEquals(dataSourceException, (result as Err).error)
    }

    @Test
    fun `getForecast returns error if dataSource throws an exception`() = runBlockingTest {
        val dataSourceException = Exception("Data source exception")
        coEvery { weatherDataSource.getForecast(latLng, measurementUnit) } throws dataSourceException

        val result = try {
            weatherRepository.getForecast(latLng)
        } catch (e: Exception) {
            Err(e)
        }

        assertTrue(result is Err)
        assertEquals(dataSourceException, (result as Err).error)
    }

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }
}
