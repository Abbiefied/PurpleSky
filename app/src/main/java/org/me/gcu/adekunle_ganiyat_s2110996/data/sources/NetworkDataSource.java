package org.me.gcu.adekunle_ganiyat_s2110996.data.sources;

import android.util.Log;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.util.AppExecutors;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.NetworkUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class NetworkDataSource {

    private static final String TAG = NetworkDataSource.class.getSimpleName();

    public interface WeatherCallback<T> {
        void onSuccess(T data);
        void onFailure(String message);
    }

    public void fetchCurrentWeather(String locationId, WeatherCallback<CurrentWeather> callback) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                String url = NetworkUtils.buildCurrentWeatherUrl(locationId);
                Log.d(TAG, "Fetching current weather data from: " + url);
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                Log.d(TAG, "Received current weather response: " + response);

                if (response.startsWith("{")) {
                    // JSON response
                    Log.e(TAG, "Unexpected JSON response: " + response);
                    AppExecutors.getInstance().mainThread().execute(() -> callback.onFailure("Unexpected JSON response"));
                } else {
                    // XML response
                    CurrentWeather currentWeather = NetworkUtils.parseCurrentWeatherXml(response);
                    Log.d(TAG, "Parsed current weather data: " + currentWeather);
                    AppExecutors.getInstance().mainThread().execute(() -> callback.onSuccess(currentWeather));
                }
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error fetching current weather: " + e.getMessage());
                AppExecutors.getInstance().mainThread().execute(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    public void fetchWeatherForecast(String locationId, WeatherCallback<List<Forecast>> callback) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                String url = NetworkUtils.buildForecastUrl(locationId);
                Log.d(TAG, "Fetching forecast weather data from: " + url);
                String xmlResponse = NetworkUtils.getResponseFromHttpUrl(url);
                Log.d(TAG, "Received forecast weather XML data: " + xmlResponse); // Log the raw XML data
                List<Forecast> forecastList = NetworkUtils.parseForecastXml(xmlResponse);
                Log.d(TAG, "Received parsed forecast weather data: " + forecastList); // Log the parsed data
                AppExecutors.getInstance().mainThread().execute(() -> callback.onSuccess(forecastList));
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error fetching weather forecast: " + e.getMessage());
                AppExecutors.getInstance().mainThread().execute(() -> callback.onFailure(e.getMessage()));
            }
        });
    }
}