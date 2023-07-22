package com.arvizu.openweather.common.util.helpers

import androidx.datastore.core.Serializer
import com.arvizu.openweather.common.data.model.AppSettings
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.InputStream
import java.io.OutputStream

object AppSettingsSerializer: Serializer<AppSettings> {
    override val defaultValue: AppSettings
        get() = AppSettings(
            locationEntity = null
        )
    override suspend fun readFrom(input: InputStream): AppSettings {
        return try {
            Json.decodeFromString(
                deserializer = AppSettings.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            Timber.e(e)
            defaultValue
        }
    }
    override suspend fun writeTo(t: AppSettings, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = AppSettings.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }

}