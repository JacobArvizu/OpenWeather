package com.arvizu.openweather.feature.weather.data.remote.api

import com.arvizu.openweather.feature.weather.data.remote.model.CurrentWeatherApiResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi

class CurrentWeatherApiResponseJsonAdapter(moshi: Moshi) : JsonAdapter<CurrentWeatherApiResponse>() {

    private val options: JsonReader.Options = JsonReader.Options.of(*NAMES)
    private val delegate: JsonAdapter<CurrentWeatherApiResponse> = moshi.adapter(CurrentWeatherApiResponse::class.java, emptySet(), NAMES[0])

    override fun fromJson(reader: JsonReader): CurrentWeatherApiResponse {

        val currentWeatherApiResponse = delegate.fromJson(reader)
            ?: throw JsonDataException("Failed to parse CurrentWeatherApiResponse")
        val temperature = currentWeatherApiResponse.main?.temp
            ?: throw JsonDataException("Missing required value: temperature")
        val date = currentWeatherApiResponse.dateTime
            ?: throw JsonDataException("Missing required value: date")

        return currentWeatherApiResponse
    }

    override fun toJson(writer: JsonWriter, value: CurrentWeatherApiResponse?) {
        delegate.toJson(writer, value)
    }

    companion object {
        private val NAMES = arrayOf("temp", "dateTime")
    }
}
