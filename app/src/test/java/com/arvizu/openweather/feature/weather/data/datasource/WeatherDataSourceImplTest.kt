package com.arvizu.openweather.feature.weather.data.datasource

import com.arvizu.openweather.feature.weather.data.remote.api.WeatherApiService
import com.arvizu.openweather.feature.weather.data.remote.model.CurrentWeatherApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastListApiResponse
import com.arvizu.openweather.feature.weather.util.exceptions.WeatherApiError
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
class WeatherDataSourceImplTest {

    @Mock
    lateinit var weatherApiService: WeatherApiService

    private lateinit var weatherDataSource: WeatherDataSourceImpl

    private val latLng = Pair(52.5200, 13.4050)
    private val measurementUnit = "metric"

    @Before
    fun setUp() {
        weatherDataSource = WeatherDataSourceImpl(weatherApiService)
    }

    // Ensure that getForecast data source properly handles a successful response
    @Test
    fun `getForecast returns forecast list`() = runBlocking {
        // Mocking the expected response
        val expectedForecastListApiResponse = ForecastListApiResponse(
            list = listOf(
                ForecastApiResponse(
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
            ),
            city = ForecastListApiResponse.City(
                coord = ForecastListApiResponse.City.Coord(
                    lat = 34.0522,
                    lon = -118.2437
                ),
                country = "US",
                id = 1,
                name = "Los Angeles",
                population = 1000000,
                sunrise = 1625500000,
                sunset = 1625550000,
                timezone = -25200
            )
        )
        val expectedResponse: Response<ForecastListApiResponse> = Response.success(expectedForecastListApiResponse)

        // Given
        `when`(weatherApiService.getWeatherData(latLng.first, latLng.second, measurementUnit))
            .thenReturn(expectedResponse)

        // When
        val result = weatherDataSource.getForecast(latLng, measurementUnit)

        // Then
        assertEquals(expectedForecastListApiResponse, (result as? Ok)?.value)
    }

    // Ensure that getCurrentWeather data source properly handles a successful response
    @Test
    fun `getCurrentWeather returns current weather`() = runBlocking {
        // Mocking the expected respons
        val expectedCurrentWeatherApiResponse = CurrentWeatherApiResponse(
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

        val expectedResponse: Response<CurrentWeatherApiResponse> = Response.success(expectedCurrentWeatherApiResponse)

        // Given
        Mockito.`when`(weatherApiService.getCurrentWeather(latLng.first, latLng.second, measurementUnit))
            .thenReturn(expectedResponse)

        // When
        val result = weatherDataSource.getCurrentWeather(latLng, measurementUnit)

        // Then
        assertEquals(expectedCurrentWeatherApiResponse, (result as? Ok)?.value)
    }


    // Ensure that getForecast data source properly handles an unsuccessful response

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(400, WeatherApiError.BadRequest),
            arrayOf(401, WeatherApiError.Unauthorized),
            arrayOf(404, WeatherApiError.NotFound),
            arrayOf(429, WeatherApiError.TooManyRequests),
            arrayOf(500, WeatherApiError.InternalServerError)
        )
    }

    // Ensure that getForecast data source properly handles an unsuccessful response for all
    // provided error codes
    @Test
    fun `getForecast returns correct error`() = runBlocking {

        data().forEach {
            val errorCode = it[0] as Int
            it[1] as WeatherApiError
            // Mocking the expected response
            val expectedResponse: Response<ForecastListApiResponse> = Response.error(errorCode, Mockito.mock(ResponseBody::class.java))

            // Given
            `when`(weatherApiService.getWeatherData(latLng.first, latLng.second, measurementUnit))
                .thenReturn(expectedResponse)

            // When
            val result = weatherDataSource.getForecast(latLng, measurementUnit)

            // Then
            assertTrue(result is Err)
        }
    }

    // Ensure that getCurrentWeather data source properly handles an unsuccessful response for all
    // provided error codes
    @Test
    fun `getCurrentWeather returns correct error`() = runBlocking {

        data().forEach {
            val errorCode = it[0] as Int
            it[1] as WeatherApiError
            // Mocking the expected response
            val expectedResponse: Response<CurrentWeatherApiResponse> = Response.error(errorCode, Mockito.mock(ResponseBody::class.java))

            // Given
            `when`(weatherApiService.getCurrentWeather(latLng.first, latLng.second, measurementUnit))
                .thenReturn(expectedResponse)

            // When
            val result = weatherDataSource.getCurrentWeather(latLng, measurementUnit)

            // Then
            assertTrue(result is Err)
        }
    }

}