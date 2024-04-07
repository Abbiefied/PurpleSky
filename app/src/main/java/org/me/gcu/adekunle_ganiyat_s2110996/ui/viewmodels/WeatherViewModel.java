package org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel {
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<CurrentWeather> currentWeatherLiveData;
    private final MutableLiveData<List<Forecast>> weatherForecastLiveData;

    public WeatherViewModel(Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application);
        currentWeatherLiveData = new MutableLiveData<>();
        weatherForecastLiveData = new MutableLiveData<>();
    }

    public LiveData<CurrentWeather> getCurrentWeather() {
        return currentWeatherLiveData;
    }

    public LiveData<List<Forecast>> getWeatherForecast() {
        return weatherForecastLiveData;
    }

    public void fetchWeatherData(Location location) {
        String locationName = location.getName();
        String locationId = location.getLocationIdByName(locationName);
        weatherRepository.fetchCurrentWeather(locationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                currentWeatherLiveData.setValue(currentWeather);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherViewModel", "Failed to fetch current weather: " + errorMessage);
            }
        });

        weatherRepository.fetchWeatherForecast(locationId, new WeatherRepository.WeatherCallback<List<Forecast>>() {

            @Override
            public void onSuccess(List<Forecast> weatherForecast) {
                weatherForecastLiveData.setValue(weatherForecast);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherViewModel", "Failed to fetch weather forecast: " + errorMessage);
            }
        });
    }

    private void handleError(String errorMessage) {
        Log.e("MainActivity", errorMessage);
        // Handle error scenario
    }
}