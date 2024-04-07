package org.me.gcu.adekunle_ganiyat_s2110996.data.repositories;

import android.content.Context;
import android.util.Log;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.LocalDataSource;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.NetworkDataSource;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.AppExecutors;

import java.util.List;

public class WeatherRepository {
    private static final String TAG = WeatherRepository.class.getSimpleName();

    private final NetworkDataSource networkDataSource;
    private final LocalDataSource localDataSource;
    private final AppExecutors appExecutors;

    public WeatherRepository(Context context) {
        networkDataSource = new NetworkDataSource();
        localDataSource = new LocalDataSource(context);
        appExecutors = AppExecutors.getInstance();
    }

    public void fetchWeatherForecast(String locationId, WeatherCallback<List<Forecast>> callback) {
        appExecutors.networkIO().execute(() -> {
            networkDataSource.fetchWeatherForecast(locationId, new NetworkDataSource.WeatherCallback<List<Forecast>>() {
                @Override
                public void onSuccess(List<Forecast> data) {
                    appExecutors.mainThread().execute(() -> {
                        callback.onSuccess(data);
                        Log.d("WeatherRepository", "Forecast: " + data);
                    });
                    appExecutors.diskIO().execute(() -> {
                        localDataSource.getWeatherForecast(locationId);
                    });
                }

                @Override
                public void onFailure(String message) {
                    appExecutors.diskIO().execute(() -> {
                        List<Forecast> cachedData = localDataSource.getWeatherForecast(locationId);
                        appExecutors.mainThread().execute(() -> {
                            if (cachedData != null && !cachedData.isEmpty()) {
                                callback.onSuccess(cachedData);
                            } else {
                                callback.onFailure(message);
                                Log.d("WeatherRepository", "Error fetching weather forecast: " + message);
                            }
                        });
                    });
                }
            });
        });
    }

    public void fetchCurrentWeather(String locationId, WeatherCallback<CurrentWeather> callback) {
        appExecutors.networkIO().execute(() -> {
            networkDataSource.fetchCurrentWeather(locationId, new NetworkDataSource.WeatherCallback<CurrentWeather>() {
                @Override
                public void onSuccess(CurrentWeather data) {
                    appExecutors.mainThread().execute(() -> {
                        callback.onSuccess(data);
                        Log.d("WeatherRepository", "Forecast: " + data);
                    });
                    appExecutors.diskIO().execute(() -> {
                        localDataSource.saveCurrentWeather(locationId, data);
                    });
                }

                @Override
                public void onFailure(String message) {
                    appExecutors.diskIO().execute(() -> {
                        CurrentWeather cachedData = localDataSource.getCurrentWeather(locationId);
                        appExecutors.mainThread().execute(() -> {
                            if (cachedData != null) {
                                callback.onSuccess(cachedData);
                            } else {
                                callback.onFailure(message);
                                Log.d("WeatherRepository", "Error fetching current weather: " + message);
                            }
                        });
                    });
                }
            });
        });
    }


    public interface WeatherCallback<T> {
        void onSuccess(T data);

        void onFailure(String message);
    }
}