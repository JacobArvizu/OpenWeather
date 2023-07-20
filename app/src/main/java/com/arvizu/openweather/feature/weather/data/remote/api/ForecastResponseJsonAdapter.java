package com.arvizu.openweather.feature.weather.data.remote.api;

import androidx.annotation.NonNull;

import com.arvizu.openweather.feature.weather.data.remote.model.ForecastApiResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.JsonWriter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

/*
 * A more verbose validation adapter for the ForecastApiResponse class. Since we don't have any
 * control over the API response, we need to make sure that the response is valid to our expected
 * format. This adapter will throw an exception which then can gracefully propagated to the
 * appropriate layer.
 */
public class ForecastResponseJsonAdapter extends JsonAdapter<ForecastApiResponse> {
    private final JsonAdapter<ForecastApiResponse> delegate;

    public ForecastResponseJsonAdapter(Moshi moshi) {
        this.delegate = moshi.adapter(ForecastApiResponse.class).nullSafe();
    }

    @Override
    public ForecastApiResponse fromJson(@NonNull JsonReader reader) throws IOException {
        ForecastApiResponse foreCastApiResponse = delegate.fromJson(reader);

        if (foreCastApiResponse != null) {
            ForecastApiResponse.Main main = foreCastApiResponse.getMain();
            if(foreCastApiResponse.getDateTime() == null) {
                throw new JsonDataException("Date is null");
            }
            if (main == null) {
                throw new JsonDataException("Main Weather is null");
            }
            if (main.getTemp() == null) {
                throw new JsonDataException("Temperature is null");
            }
            if (foreCastApiResponse.getWeather() == null) {
                throw new JsonDataException("Weather is null");
            }
            if (foreCastApiResponse.getWeather().isEmpty()) {
                throw new JsonDataException("Weather is empty");
            }
            for (ForecastApiResponse.Weather weather : foreCastApiResponse.getWeather()) {
                if (weather.getMain() == null) {
                    throw new JsonDataException("Description is null");
                }
                if (weather.getIcon() == null) {
                    throw new JsonDataException("Icon is null");
                }
            }
        }

        return foreCastApiResponse;
    }

    @Override
    public void toJson(JsonWriter writer, ForecastApiResponse value) throws IOException {
        delegate.toJson(writer, value);
    }
}