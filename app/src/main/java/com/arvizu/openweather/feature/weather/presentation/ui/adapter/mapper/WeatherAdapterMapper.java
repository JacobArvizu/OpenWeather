package com.arvizu.openweather.feature.weather.presentation.ui.adapter.mapper;

import com.arvizu.openweather.common.util.constants.AppConstants;
import com.arvizu.openweather.common.util.helpers.SharedPreferencesHelper;
import com.arvizu.openweather.feature.weather.dto.WeatherDTO;
import com.arvizu.openweather.feature.weather.presentation.ui.adapter.model.WeatherCard;

import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WeatherAdapterMapper {

    private final SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public WeatherAdapterMapper(SharedPreferencesHelper sharedPreferencesHelper) {
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    public WeatherCard mapWeatherDTOtoWeatherCard(WeatherDTO weatherDTO) {

        String temperature;
        String windSpeed;
        String humidity = Optional.of("Humidity: " +  weatherDTO.getHumidity()).map(h -> h + "%").orElse("N/A");
        String cloudiness = Optional.of("Cloudiness: " + weatherDTO.getCloudiness()).map(c -> c + "%").orElse("N/A");
        String description = Optional.ofNullable(weatherDTO.getDescription()).orElse("N/A");
        String iconUrl = Optional.ofNullable(weatherDTO.getIconUrl()).orElse("N/A");
        String time = Optional.ofNullable(weatherDTO.getTimeOfDay()).orElse("N/A");
        if (weatherDTO.getUnit().equals(AppConstants.MEASUREMENT_UNIT_METRIC)) {
            temperature = Optional.of("Temperature: " + weatherDTO.getTemperature()).map(t -> t + "°C").orElse("N/A");
            windSpeed = Optional.of("Wind Speed: " + weatherDTO.getWindSpeed()).map(ws -> ws + " m/s").orElse("N/A");
        } else {
            temperature = Optional.of("Temperature: " + weatherDTO.getTemperature()).map(t -> t + "°F").orElse("N/A");
            windSpeed = Optional.of("Wind Speed: " + weatherDTO.getWindSpeed()).map(ws -> ws + " mph").orElse("N/A");
        }

        return new WeatherCard(weatherDTO.getDate(), temperature, windSpeed, humidity, cloudiness, iconUrl, description, time);
    }
}
