package com.arvizu.openweather.common.util.helpers;

import android.content.SharedPreferences;

import com.arvizu.openweather.common.util.constants.AppConstants;
import com.arvizu.openweather.feature.weather.util.constants.WeatherPreferenceKeys;

import javax.inject.Inject;

public class SharedPreferencesHelperImpl implements SharedPreferencesHelper {
    private final SharedPreferences sharedPreferences;
    @Inject
    public SharedPreferencesHelperImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
    public String getPreferredMeasurementUnit() {
        return sharedPreferences.getString(WeatherPreferenceKeys.PREFERRED_UNIT_MEASUREMENT, AppConstants.MEASUREMENT_UNIT_IMPERIAL); // default to true
    }

    public void setPreferredMeasurementUnit(String unit) {
        sharedPreferences.edit().putString(WeatherPreferenceKeys.PREFERRED_UNIT_MEASUREMENT, unit).apply();
    }
}