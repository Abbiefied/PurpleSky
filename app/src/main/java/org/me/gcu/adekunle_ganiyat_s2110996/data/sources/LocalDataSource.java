package org.me.gcu.adekunle_ganiyat_s2110996.data.sources;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LocalDataSource {

    private static final String PREF_NAME = "weather_pref";
    private static final String KEY_CURRENT_WEATHER = "current_weather";
    private static final String KEY_FORECAST = "forecast";

    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public LocalDataSource(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveCurrentWeather(String locationId, CurrentWeather currentWeather) {
        String json = gson.toJson(currentWeather);
        sharedPreferences.edit().putString(KEY_CURRENT_WEATHER + "_" + locationId, json).apply();
    }

    public CurrentWeather getCurrentWeather(String locationId) {
        String json = sharedPreferences.getString(KEY_CURRENT_WEATHER + "_" + locationId, null);
        if (json != null) {
            return gson.fromJson(json, CurrentWeather.class);
        }
        return null;
    }

    public void saveWeatherForecast(String locationId, List<Forecast> forecastList) {
        String json = gson.toJson(forecastList);
        sharedPreferences.edit().putString(KEY_FORECAST + "_" + locationId, json).apply();
    }

    public List<Forecast> getWeatherForecast(String locationId) {
        String json = sharedPreferences.getString(KEY_FORECAST + "_" + locationId, null);
        if (json != null) {
            Type type = new TypeToken<List<Forecast>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
}