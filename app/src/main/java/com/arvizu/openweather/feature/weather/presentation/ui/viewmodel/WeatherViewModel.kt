package com.arvizu.openweather.feature.weather.presentation.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.arvizu.openweather.NetworkViewModel
import com.arvizu.openweather.feature.weather.presentation.model.WeatherUIModel
import com.arvizu.openweather.feature.weather.use_case.WeatherUseCases
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.google.android.libraries.places.widget.Autocomplete
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    private val weatherUseCases: WeatherUseCases
) : NetworkViewModel() {

    private val _weather = MutableLiveData<WeatherUIModel>()
    val weather: LiveData<WeatherUIModel> get() = _weather

    private val _placeAddress = MutableLiveData<String>()
    val placeAddress: LiveData<String> get() = _placeAddress

    // Launch the getWeather use case under the viewModel scope with the coroutine exception handler
    private fun getWeather(latLng: Pair<Double, Double>) = launchSafe {
        _loading.value = true
        when (val result = weatherUseCases.getWeather.execute(latLng)) {
            is Ok -> {
                val uiModel = result.value
                _weather.value = uiModel
            }
            // Even though NetworkViewModel's coroutine exception handler serves as a safety net,
            // we should aim to capture errors here as this means we caught them in a structured manner.
            is Err -> {
                _errorObs.value = result.error
            }
        }
        _loading.value = false
    }

    fun processPlaceFromIntent(data: Intent) {
        val place = Autocomplete.getPlaceFromIntent(data)
        if (place.latLng == null) {
            _errorObs.value = Throwable("No latitude/longitude found for place: $place")
        }
        getWeather(place.latLng!!.latitude to place.latLng!!.longitude)
        _placeAddress.value = place.address
    }

    fun handleErrorFromIntent(data: Intent) {
        val status = Autocomplete.getStatusFromIntent(data)
        Timber.e(status.statusMessage)
    }
}