package com.arvizu.openweather.feature.weather.presentation.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.arvizu.openweather.NetworkViewModel
import com.arvizu.openweather.common.use_case.AppUseCases
import com.arvizu.openweather.common.util.helpers.SharedPreferencesHelper
import com.arvizu.openweather.feature.weather.presentation.ui.adapter.mapper.WeatherAdapterMapper
import com.arvizu.openweather.feature.weather.presentation.ui.adapter.model.WeatherCard
import com.arvizu.openweather.feature.weather.use_case.WeatherUseCases
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.google.android.libraries.places.widget.Autocomplete
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import timber.log.Timber
import javax.inject.Inject

/*
 * This viewModel is responsible for handling the business logic for the weather feature.
 * It extends the networkViewModel, which provides a coroutine scope for launching coroutines with
 * an exception handler that will post the error to the errorObs although all use cases should
 * return error results instead of throwing exceptions as to not use exceptions for control flow.
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val weatherUseCases: WeatherUseCases,
    private val appUseCases: AppUseCases,
    private val weatherAdapterMapper: WeatherAdapterMapper,
    private val _sharedPreferencesHelper: SharedPreferencesHelper,

    ) : NetworkViewModel() {
    companion object {
        private const val KEY_LAT_LNG = "current_place"
    }

    private val _hasCachedLocation = MutableLiveData<Boolean>()
    val hasCachedLocation: LiveData<Boolean> get() = _hasCachedLocation

    private val _currentWeatherCard = MutableLiveData<WeatherCard>()
    val currentWeatherCard: LiveData<WeatherCard> = _currentWeatherCard

    private val _forecastCards = MutableLiveData<List<WeatherCard>>()
    val forecastCards: LiveData<List<WeatherCard>> = _forecastCards

    private val _placeAddress = MutableLiveData<String>()
    val placeAddress: LiveData<String> get() = _placeAddress
    private val sharedPreferencesHelper get() = _sharedPreferencesHelper

    private suspend fun getForecast(latLng: Pair<Double, Double>) {
        when (val result = weatherUseCases.getForecast.execute(latLng)) {
            is Ok -> {
                val weatherCardList = result.value.map { weatherDto ->
                    weatherAdapterMapper.mapWeatherDTOtoWeatherCard(weatherDto)
                }
                _forecastCards.postValue(weatherCardList)
            }

            is Err -> {
                _errorObs.postValue(result.error)
            }
        }
    }

    private suspend fun getCurrentWeather(latLng: Pair<Double, Double>) {
        when (val result = weatherUseCases.getCurrentWeather.execute(latLng)) {
            is Ok -> {
                val weatherCard = weatherAdapterMapper.mapWeatherDTOtoWeatherCard(result.value)
                _currentWeatherCard.postValue(weatherCard)
            }

            is Err -> {
                _errorObs.postValue(result.error)
            }
        }
    }

    /*
     * Launches two coroutines to get the current weather and forecast data, these functions
     * will be launched under the viewModel scope and run in parallel.
     */
    fun getAllWeatherData() = launchSafe {
        val latLng = savedStateHandle.get<Pair<Double, Double>>(KEY_LAT_LNG)!!
        _loading.value = true
        val getCurrentWeather = async { getCurrentWeather(latLng) }
        val getForecast = async { getForecast(latLng) }
        getCurrentWeather.await()
        getForecast.await()
        appUseCases.setLocationPreferencesUseCase.execute(latLng.first, latLng.second, _placeAddress.value?:"")
        _loading.postValue(false)
    }

    fun setCurrentLocation(latLng: Pair<Double, Double>) {
        savedStateHandle[KEY_LAT_LNG] = latLng
        getAllWeatherData()
    }

    fun processPlaceFromIntent(data: Intent) {
        val place = Autocomplete.getPlaceFromIntent(data)
        if (place.latLng == null) {
            _errorObs.postValue(Throwable("No latitude/longitude found for place"))
        }
        savedStateHandle[KEY_LAT_LNG] = Pair(place.latLng!!.latitude, place.latLng!!.longitude)
        _placeAddress.postValue(place.address)
        getAllWeatherData()
    }

    fun handleErrorFromIntent(data: Intent) {
        val status = Autocomplete.getStatusFromIntent(data)
        Timber.e(status.statusMessage)
        _errorObs.postValue(Throwable(status.statusMessage))
    }

    fun changeWeatherUnit(measurementUnitMetric: String) {
        if (measurementUnitMetric == sharedPreferencesHelper.preferredMeasurementUnit) {
            return
        }
        sharedPreferencesHelper.preferredMeasurementUnit = measurementUnitMetric
        // if there is a place in the savedStateHandle, then we need to update the weather data
        if (savedStateHandle.contains(KEY_LAT_LNG)) {
            Timber.d("Changing weather unit to $measurementUnitMetric")
            getAllWeatherData()
        }
    }

    fun checkForCachedLocation() = launchSafe {
        // Can set to inline but easier to debug this way
        val result = appUseCases.getLocationPreferencesUseCase.execute()
        when(result) {
            is Ok -> {
                val locationEntity = result.value
                _placeAddress.postValue(locationEntity.city)
                savedStateHandle[KEY_LAT_LNG] = Pair(locationEntity.latitude, locationEntity.longitude)
                _hasCachedLocation.value = true
            }
            is Err -> {
                Timber.d("No cached location found")
                _hasCachedLocation.value = false
            }
        }
    }
}