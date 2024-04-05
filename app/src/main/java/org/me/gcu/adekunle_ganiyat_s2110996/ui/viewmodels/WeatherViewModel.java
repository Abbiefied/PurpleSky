package org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;

import java.util.List;

public class WeatherViewModel extends AndroidViewModel {

    private final WeatherRepository weatherRepository;
    private final MutableLiveData<CurrentWeather> currentWeatherLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Forecast>> forecastLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Forecast> selectedForecastLiveData = new MutableLiveData<>();

    public WeatherViewModel(Application application) {
        super(application);
        weatherRepository = new WeatherRepository(application.getApplicationContext());
    }

    public LiveData<CurrentWeather> getCurrentWeather() {
        return currentWeatherLiveData;
    }

    public LiveData<List<Forecast>> getForecast() {
        return forecastLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public LiveData<Forecast> getSelectedForecast() {
        return selectedForecastLiveData;
    }

    public void setSelectedForecast(Forecast forecast) {
        selectedForecastLiveData.setValue(forecast);
    }

    public void fetchCurrentWeather(String locationId) {
        weatherRepository.fetchCurrentWeather(locationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
            @Override
            public void onSuccess(CurrentWeather data) {
                currentWeatherLiveData.postValue(data);
            }

            @Override
            public void onFailure(String message) {
                errorMessageLiveData.postValue(message);
            }
        });
    }

    public void fetchWeatherForecast(String locationId) {
        weatherRepository.fetchWeatherForecast(locationId, new WeatherRepository.WeatherCallback<List<Forecast>>() {
            @Override
            public void onSuccess(List<Forecast> data) {
                forecastLiveData.postValue(data);
            }

            @Override
            public void onFailure(String message) {
                errorMessageLiveData.postValue(message);
            }
        });
    }
}