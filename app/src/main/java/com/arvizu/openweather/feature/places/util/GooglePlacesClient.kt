package com.arvizu.openweather.feature.places.util

import com.arvizu.openweather.BuildConfig
import android.content.Context
import com.google.android.libraries.places.api.Places
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GooglePlacesClient @Inject constructor(
    @ApplicationContext private val context: Context
) {

    init {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.PLACES_API_KEY, Locale.getDefault())
        }
    }
}