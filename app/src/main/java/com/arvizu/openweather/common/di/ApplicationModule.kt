package com.arvizu.openweather.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.arvizu.openweather.common.data.model.AppSettings
import com.arvizu.openweather.common.util.helpers.AppSettingsSerializer
import com.arvizu.openweather.common.util.helpers.SharedPreferencesHelper
import com.arvizu.openweather.common.util.helpers.SharedPreferencesHelperImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

private val Context.dataStore by dataStore(
    fileName = "user_preferences",
    serializer = AppSettingsSerializer,
)
@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(@ApplicationContext context: Context): SharedPreferencesHelper {
        // initialize shared preferences
        val sharedPreferences = context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
        return SharedPreferencesHelperImpl(sharedPreferences)
    }
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<AppSettings> {
        return context.dataStore
    }

    // Since fusedLocationProviderClient is an internal android class we can provide it in the
    // common module to provide app wide access to it.
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

}