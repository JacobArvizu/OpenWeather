import com.arvizu.openweather.feature.weather.data.mapper.WeatherResponseMapper
import com.arvizu.openweather.feature.weather.data.remote.model.CurrentWeatherApiResponse
import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


/*
 * This class is responsible for mapping and transforming the ForecastApiResponse and WeatherApiResponse to WeatherDTOModel.
 * This mapper class is the most important piece to test as it is responsible for mapping the response to the DTO
 * object which is used for the entire feature. We want to not only handle exceptions but
 * also ensure that the data is valid and in the format we expect.
 */
class WeatherResponseMapperTest {

    @InjectMockKs
    var weatherResponseMapper = WeatherResponseMapper()

    private lateinit var validForecastApiResponse: ForecastApiResponse
    private lateinit var invalidForecastApiResponse: ForecastApiResponse
    private lateinit var validCurrentWeatherApiResponse: CurrentWeatherApiResponse
    private lateinit var invalidCurrentWeatherApiResponse: CurrentWeatherApiResponse

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)

        // Here we initialize the responses. Adjust this data according to your actual API response structure and expected values
        validForecastApiResponse = ForecastApiResponse(
            base = "stations",
            clouds = ForecastApiResponse.Clouds(all = 1),
            responseCode = 200,
            coordinates = ForecastApiResponse.Coord(latitude = 34.0522, longitude = -118.2437),
            dateTime = 1625500000,
            id = 1,
            main = ForecastApiResponse.Main(feelsLike = 35.0, humidity = 30, pressure = 1013, temp = 36.7, tempMax = 38.0, tempMin = 34.5),
            name = "Los Angeles",
            sys = ForecastApiResponse.Sys(country = "US", id = 1, sunrise = 1625500000, sunset = 1625550000, type = 1),
            timezone = -25200,
            visibility = 10000,
            weather = listOf(ForecastApiResponse.Weather(description = "clear sky", icon = "01d", id = 1, main = "Clear")),
            wind = ForecastApiResponse.Wind(deg = 320, gust = 11.3, speed = 7.2)
        )

        invalidForecastApiResponse = ForecastApiResponse(
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

        validCurrentWeatherApiResponse = CurrentWeatherApiResponse(
            base = "stations",
            clouds = CurrentWeatherApiResponse.Clouds(all = 20),
            responseCode = 200,
            coordinates = CurrentWeatherApiResponse.Coord(latitude = 35.6895, longitude = 139.6917),
            dateTime = 1560358331,
            id = 1851632,
            main = CurrentWeatherApiResponse.Main(feelsLike = 270.15, humidity = 81, pressure = 1016, temp = 281.52, tempMax = 283.71, tempMin = 279.82),
            name = "Tokyo",
            sys = CurrentWeatherApiResponse.Sys(country = "JP", id = 8074, sunrise = 1560292963, sunset = 1560346453, type = 1),
            timezone = 32400,
            visibility = 10000,
            weather = listOf(CurrentWeatherApiResponse.Weather(description = "clear sky", icon = "01d", id = 800, main = "Clear")),
            wind = CurrentWeatherApiResponse.Wind(deg = 80, gust = 8.2, speed = 6.7)
        )

        invalidCurrentWeatherApiResponse = CurrentWeatherApiResponse(
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
    }

    @Test
    fun `mapToDTO returns Ok with ForecastApiResponse if all required fields are present`() {
        val result = weatherResponseMapper.mapToDTO(validForecastApiResponse, 0, "metric")

        assertTrue(result is Ok)
    }

    @Test
    fun `mapToDTO returns Err with ForecastApiResponse if a required field is missing`() {
        val result = weatherResponseMapper.mapToDTO(invalidForecastApiResponse, 0, "metric")

        assertTrue(result is Err)
    }

    @Test
    fun `mapToDTO returns Ok with CurrentWeatherApiResponse if all required fields are present`() {
        val result = weatherResponseMapper.mapToDTO(validCurrentWeatherApiResponse, "metric")

        assertTrue(result is Ok)
    }

    @Test
    fun `mapToDTO returns Err with CurrentWeatherApiResponse if a required field is missing`() {
        val result = weatherResponseMapper.mapToDTO(invalidCurrentWeatherApiResponse, "metric")

        assertTrue(result is Err)
    }
}
