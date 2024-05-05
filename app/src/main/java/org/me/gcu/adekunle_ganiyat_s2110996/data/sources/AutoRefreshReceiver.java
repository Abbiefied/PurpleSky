//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.data.sources;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceManager;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherViewModel;

import java.util.Calendar;
import java.util.List;

public class AutoRefreshReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int refreshHour = sharedPreferences.getInt("refresh_hour", 8);
        int refreshMinute = sharedPreferences.getInt("refresh_minute", 0);

        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        if (currentHour == refreshHour && currentMinute == refreshMinute) {
            // Refresh weather data for the default location
            String defaultLocationId = Location.getDefaultLocationId();
            if (defaultLocationId != null && !defaultLocationId.isEmpty()) {
                Application application = (Application) context.getApplicationContext();
                WeatherViewModel weatherViewModel = new ViewModelProvider((ViewModelStoreOwner) application).get(WeatherViewModel.class);
                weatherViewModel.refreshWeatherData(defaultLocationId);
            }
        }
    }
}