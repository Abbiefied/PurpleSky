//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.data.sources;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LocalDataSource {

    private static final String PREF_NAME = "weather_pref";
    private static final String KEY_CURRENT_WEATHER = "current_weather";
    private static final String KEY_FORECAST = "forecast";
    private static final String PREF_RECENT_LOCATIONS = "pref_recent_locations";
    private static final String KEY_RECENT_LOCATIONS = "key_recent_locations";

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public LocalDataSource(Context context) {
        this.context = context;
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



    public List<Location> getRecentLocations() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_RECENT_LOCATIONS, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RECENT_LOCATIONS, null);
        if (json != null) {
            Type type = new TypeToken<List<Location>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void addRecentLocation(Location location) {
        List<Location> recentLocations = getRecentLocations();
        if (recentLocations == null) {
            recentLocations = new ArrayList<>();
        }

        // Remove the location if it already exists
        recentLocations.removeIf(loc -> loc.getId() == location.getId());

        // Add the new location to the top of the list
        recentLocations.add(0, location);

        // Limit the list to the last 3 locations
        if (recentLocations.size() > 2) {
            recentLocations = recentLocations.subList(0, 3);
        }

        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_RECENT_LOCATIONS, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_RECENT_LOCATIONS, new Gson().toJson(recentLocations));
        editor.apply();
    }

    public void deleteCachedData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}