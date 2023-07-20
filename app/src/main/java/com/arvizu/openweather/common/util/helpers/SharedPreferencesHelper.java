package com.arvizu.openweather.common.util.helpers;

/*
 * A simple helper to create property accessors for SharedPreferences. We could use a more generic
 * approach such as using tpe and keys as parameters, however having clear reference for
 * each property provides better usability and readability.
 */
public interface SharedPreferencesHelper {
    String getPreferredMeasurementUnit();
    void setPreferredMeasurementUnit(String unitMeasurement);
}
