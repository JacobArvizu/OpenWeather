package com.arvizu.openweather.feature.weather.util.extensions

import com.arvizu.openweather.feature.weather.util.exceptions.WeatherApiError
import com.arvizu.openweather.feature.weather.util.exceptions.WeatherApiException
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import retrofit2.Response

/*
 * Higher order extension function to process a response from the API in order to gracefully handle
 * expected errors. This works by taking in a suspend function and checks first with the built in Retrofit Response<T>.
 * Then it checks against the openweather API error codes provided in the documentation.
 */
suspend fun <T, R> Response<T>.processResponse(processor: suspend (T) -> Result<R, Exception>): Result<R, Exception> {
    // This code is still somewhat tightly coupled to the openweather API because of its
    // explicit Exceptions and error codes. This could be abstracted further to be more generic and
    // moved to the common module.
    if (!this.isSuccessful) {
        val error = mapErrorCode(this.code())
        return Err(WeatherApiException("Error: ${this.message()}", error))
    }

    val data = this.body() ?: return Err(WeatherApiException("Error: response body is null"))

    return processor(data)
}

private fun mapErrorCode(errorCode: Int): WeatherApiError {
    val errorMap = mapOf(
        400 to WeatherApiError.BadRequest,
        401 to WeatherApiError.Unauthorized,
        404 to WeatherApiError.NotFound,
        429 to WeatherApiError.TooManyRequests,
        500 to WeatherApiError.InternalServerError
    )

    return errorMap[errorCode] ?: WeatherApiError.Unknown(errorCode)
}