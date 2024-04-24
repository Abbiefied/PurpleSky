package org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import org.me.gcu.adekunle_ganiyat_s2110996.activities.WeatherNotificationService;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;

public class SettingsViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> refreshSuccess = new MutableLiveData<>();
    private MutableLiveData<String> refreshError = new MutableLiveData<>();
    private MutableLiveData<Boolean> notificationsEnabled = new MutableLiveData<>();
    private Context context;

    public SettingsViewModel(Application application) {
        super(application);
        context = application.getApplicationContext();
        // Initialize the notifications enabled state
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sharedPreferences.getBoolean("notifications", true);
        notificationsEnabled.setValue(enabled);
    }

    public void refreshWeatherData(String locationId) {
        WeatherRepository weatherRepository = new WeatherRepository(context);
        weatherRepository.refreshWeatherData(locationId, new WeatherRepository.WeatherCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                refreshSuccess.postValue(true);
            }

            @Override
            public void onFailure(String message) {
                refreshError.postValue(message);
            }
        });
    }

    public LiveData<Boolean> getRefreshSuccess() {
        return refreshSuccess;
    }

    public LiveData<String> getRefreshError() {
        return refreshError;
    }

    public LiveData<Boolean> getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean enabled) {
        Log.d("SettingsViewModel", "Notifications enabled: " + enabled);
        notificationsEnabled.setValue(enabled);
        updateNotificationSettings(enabled);
    }

    private void updateNotificationSettings(boolean enabled) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notifications", enabled);
        editor.apply();

        if (enabled) {
            startWeatherNotificationService();
        } else {
            stopWeatherNotificationService();
        }
    }

    private void startWeatherNotificationService() {
        Intent intent = new Intent(context, WeatherNotificationService.class);
        context.startService(intent);
    }

    private void stopWeatherNotificationService() {
        Intent intent = new Intent(context, WeatherNotificationService.class);
        context.stopService(intent);
    }
}