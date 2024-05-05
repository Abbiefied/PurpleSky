//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.NetworkDataSource;

import java.util.List;

public class WeatherComparisonViewModel extends AndroidViewModel {
    private final WeatherRepository weatherRepository;
    private final MutableLiveData<CurrentWeather> currentWeather1LiveData;
    private final MutableLiveData<CurrentWeather> currentWeather2LiveData;
    private final MutableLiveData<List<Forecast>> weatherForecast1LiveData;
    private final MutableLiveData<List<Forecast>> weatherForecast2LiveData;
    private final MutableLiveData<AirQualityData> airQualityData1LiveData;
    private final MutableLiveData<AirQualityData> airQualityData2LiveData;

    public WeatherComparisonViewModel(Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application);
        currentWeather1LiveData = new MutableLiveData<>();
        currentWeather2LiveData = new MutableLiveData<>();
        weatherForecast1LiveData = new MutableLiveData<>();
        weatherForecast2LiveData = new MutableLiveData<>();
        airQualityData1LiveData = new MutableLiveData<>();
        airQualityData2LiveData = new MutableLiveData<>();
    }

    // Getters for LiveData objects
    public WeatherRepository getWeatherRepository() {
        return weatherRepository;
    }

    public MutableLiveData<CurrentWeather> getCurrentWeather1() {
        return currentWeather1LiveData;
    }

    public MutableLiveData<CurrentWeather> getCurrentWeather2() {
        return currentWeather2LiveData;
    }

    public MutableLiveData<List<Forecast>> getWeatherForecast1() {
        return weatherForecast1LiveData;
    }

    public MutableLiveData<List<Forecast>> getWeatherForecast2() {
        return weatherForecast2LiveData;
    }

    public MutableLiveData<AirQualityData> getAirQualityData1() {
        return airQualityData1LiveData;
    }

    public MutableLiveData<AirQualityData> getAirQualityData2() {
        return airQualityData2LiveData;
    }

    public void fetchWeatherData(Location location1, Location location2) {
        String locationId1 = String.valueOf(location1.getId());
        String locationId2 = String.valueOf(location2.getId());

        // Fetch current weather for location 1
        weatherRepository.fetchCurrentWeather(locationId1, new WeatherRepository.WeatherCallback<CurrentWeather>() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                currentWeather1LiveData.setValue(currentWeather);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherComparisonView", "Failed to fetch current weather for location 1: " + errorMessage);
            }
        });

        // Fetch current weather for location 2
        weatherRepository.fetchCurrentWeather(locationId2, new WeatherRepository.WeatherCallback<CurrentWeather>() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                currentWeather2LiveData.setValue(currentWeather);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherComparisonView", "Failed to fetch current weather for location 2: " + errorMessage);
            }
        });

        // Fetch weather forecast for location 1
        weatherRepository.fetchWeatherForecast(locationId1, new WeatherRepository.WeatherCallback<List<Forecast>>() {
            @Override
            public void onSuccess(List<Forecast> weatherForecast) {
                weatherForecast1LiveData.setValue(weatherForecast);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherComparisonView", "Failed to fetch weather forecast for location 1: " + errorMessage);
            }
        });

        // Fetch weather forecast for location 2
        weatherRepository.fetchWeatherForecast(locationId2, new WeatherRepository.WeatherCallback<List<Forecast>>() {
            @Override
            public void onSuccess(List<Forecast> weatherForecast) {
                weatherForecast2LiveData.setValue(weatherForecast);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherComparisonView", "Failed to fetch weather forecast for location 2: " + errorMessage);
            }
        });

        // Fetch air quality data for location 1
        weatherRepository.fetchAirQualityData(location1.getLatitude(), location1.getLongitude(), new NetworkDataSource.WeatherCallback<AirQualityData>() {
            @Override
            public void onSuccess(AirQualityData airQualityData) {
                airQualityData1LiveData.setValue(airQualityData);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherComparisonView", "Failed to fetch air quality data for location 1: " + errorMessage);
            }
        });

// Fetch air quality data for location 2
        weatherRepository.fetchAirQualityData(location2.getLatitude(), location2.getLongitude(), new NetworkDataSource.WeatherCallback<AirQualityData>() {
            @Override
            public void onSuccess(AirQualityData airQualityData) {
                airQualityData2LiveData.setValue(airQualityData);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("WeatherComparisonView", "Failed to fetch air quality data for location 2: " + errorMessage);
            }
        });
    }
    public void fetchSearchSuggestions(String query, NetworkDataSource.SearchCallback<List<Location>> callback) {
        weatherRepository.fetchSearchSuggestions(query, callback);
    }
}
