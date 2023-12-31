package com.arvizu.openweather.feature.weather.presentation.ui.fragment

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.arvizu.openweather.R
import com.arvizu.openweather.common.util.constants.AppConstants
import com.arvizu.openweather.common.util.helpers.FusedLocationHelper
import com.arvizu.openweather.databinding.FragmentWeatherBinding
import com.arvizu.openweather.feature.places.util.GooglePlacesClient
import com.arvizu.openweather.feature.weather.presentation.ui.adapter.WeatherCardAdapter
import com.arvizu.openweather.feature.weather.presentation.ui.adapter.model.WeatherCard
import com.arvizu.openweather.feature.weather.presentation.ui.viewmodel.WeatherViewModel
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class WeatherFragment : Fragment() {

    @Inject
    lateinit var placesClient: GooglePlacesClient

    @Inject
    lateinit var fusedLocationHelper: FusedLocationHelper

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var startForResult: ActivityResultLauncher<Intent>

    private lateinit var weatherAdapter: WeatherCardAdapter

    private val requestPermissionLauncher: ActivityResultLauncher<String> by lazy {
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            handleLocationCheck()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlePlaceResult()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeWeather()
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                ACCESS_FINE_LOCATION
            ) -> {
                handleLocationCheck()
            }
            else -> {
                requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility") // No need to override the class/view for simple use case
    private fun initViews() = with(binding) {

        viewModel.placeAddress.observe(viewLifecycleOwner) { address ->
            enterCityEditText.setText(address)
        }

        enterCityEditText.setOnFocusChangeListener { v, hasFocus ->

            if (hasFocus) {
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
                // Filtering by cities has been deprecated, but it still works
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .setTypeFilter(TypeFilter.CITIES)
                    .build(requireContext())
                startForResult.launch(intent)
            }
        }

        // Simple hack to add inline unit change button for autocomplete text view.
        // Ideal touch target size could be improved.
        enterCityEditText.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Trial and error to find best user experience size
                if (event.rawX <= (enterCityEditText.totalPaddingLeft + enterCityEditText.compoundPaddingLeft - 50)) {
                    showUnitChangeDialog()
                    v.performClick()
                    return@setOnTouchListener true
                }
            }
            return@setOnTouchListener false
        }

        weatherAdapter = WeatherCardAdapter().apply {
            onItemClick = { weather ->
                Timber.d("Weather card clicked: $weather")
            }
        }

        initRecyclerView(weatherRecyclerView)
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            recyclerView.setHasFixedSize(false)
            recyclerView.layoutManager = layoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
        recyclerView.adapter = weatherAdapter
    }

    /*
     * If the user has a cached locationEntity, we'll use that to get the weather data.
     * Otherwise we'll try to get the current locationEntity if the user has granted location permissions
     */
    private fun handleLocationCheck() {
        viewModel.hasCachedLocation.observe(viewLifecycleOwner) { hasCachedLocation ->
            if (hasCachedLocation) {
                viewModel.getAllWeatherData()
            } else {
                fetchCurrentLocation()
            }
        }
        viewModel.checkForCachedLocation()
    }

    private fun fetchCurrentLocation() {
        // Calling from lifecycleScope ensures the fusedLocationHelper suspending function will be cancelled
        // if the fragment is destroyed before the coroutine completes.
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = fusedLocationHelper.getCurrentLocation()) {
                is Ok -> {
                    result.value.let { latLng ->
                        viewModel.setCurrentLocation(latLng)
                        _binding?.enterCityEditText?.let {
                            it.setText(getString(R.string.current_location))
                            // Change the color of the text to indicate that the location is current
                            it.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_blue))
                        }
                    }
                }
                is Err -> {
                    Timber.e(result.error)
                    Toast.makeText(requireContext(), "Could not get location automatically, please check permissions", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Timber.e("Unexpected error)") // Should never happen
                }
            }
        }
    }


    private fun handlePlaceResult() {
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                binding.enterCityEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                viewModel.processPlaceFromIntent(result.data!!)
            } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
                viewModel.handleErrorFromIntent(result.data!!)
            }
            binding.enterCityEditText.clearFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.enterCityEditText.windowToken, 0)
        }
    }

    private fun observeWeather() {
        // Could use a mediatorLiveData or state flow to combine the flows into a single state
        // but for simplicity we'll just observe them separately. Allowing one call to fail and
        // the other to succeed.
        viewModel.forecastCards.observe(viewLifecycleOwner) { weatherCards ->
            setForecast(weatherCards)
        }
        viewModel.currentWeatherCard.observe(viewLifecycleOwner) { weatherCard ->
            setCurrentWeather(weatherCard)
        }
        viewModel.errorObs.observe(viewLifecycleOwner) {error ->
            showError(error)
        }
        // Loading should sync with the recyclerview loading state as that may actually take longer
        // than the network call however this is a minor UI/UX issue.
        viewModel.loading.observe(viewLifecycleOwner) {isLoading ->
            showLoading(isLoading)
        }
    }

    private fun setForecast(weatherCards: List<WeatherCard>) {
        Timber.d("Forecast: $weatherCards")
        if (weatherCards.isEmpty()) {
            return
        }
        binding.forecastHeader.visibility = View.VISIBLE
        binding.noLocationTextView.visibility = View.GONE
        binding.noLocationImageView.visibility = View.GONE
        weatherAdapter.submitList(weatherCards.subList(1, weatherCards.size))
    }

    private fun setCurrentWeather(weatherCard: WeatherCard) {
        binding.currentWeatherHeader.visibility = View.VISIBLE
        binding.currentWeatherCard.root.visibility = View.VISIBLE
        binding.currentWeatherCard.dateTextView.text = weatherCard.date
        binding.currentWeatherCard.temperatureTextView.text = weatherCard.temperature
        binding.currentWeatherCard.windSpeedTextView.text = weatherCard.windSpeed
        binding.currentWeatherCard.humidityTextView.text = weatherCard.humidity
        binding.currentWeatherCard.cloudinessTextView.text = weatherCard.cloudiness
        binding.currentWeatherCard.descriptionTextView.text = weatherCard.description
        binding.currentWeatherCard.timeOfDayTextView.text = weatherCard.timeOfDay
        binding.currentWeatherCard.weatherLogoImageView.load(weatherCard.iconUrl)
    }

    private fun showUnitChangeDialog() {
        // Design wise this should be a bottom sheet, but for simplicity we'll use an alert dialog
        AlertDialog.Builder(requireActivity())
            .setTitle("Select Unit")
            .setPositiveButton("Metric") { _, _ ->
                viewModel.changeWeatherUnit(AppConstants.MEASUREMENT_UNIT_METRIC)
            }
            .setNegativeButton("Imperial") { _, _ ->
                viewModel.changeWeatherUnit(AppConstants.MEASUREMENT_UNIT_IMPERIAL)
            }
            .show()
    }
    private fun showError(error: Throwable?) {
        Toast.makeText(requireContext(), "Something went wrong! Please Try again.", Toast.LENGTH_SHORT).show()
        error?.let { Timber.e(it.message) }
        showLoading(false)
    }

    private fun showLoading(isLoading: Boolean) {
        Timber.d("Loading: $isLoading")
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
