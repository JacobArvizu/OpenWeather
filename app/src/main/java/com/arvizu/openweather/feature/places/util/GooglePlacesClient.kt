package com.arvizu.openweather.feature.places.util

import android.content.Context
import com.arvizu.openweather.common.util.constants.NetworkConstants
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
            Places.initialize(context, NetworkConstants.PLACES_API_KEY, Locale.getDefault())
        }
    }

    val placesClient = Places.createClient(context)
}