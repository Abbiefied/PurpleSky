//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.DateUtils;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.List;
import java.util.Locale;

public class WeatherNotificationService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "WeatherNotificationChannel";

    private WeatherRepository weatherRepository;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        weatherRepository = new WeatherRepository(getApplicationContext());
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WeatherNotificationSer", "Service started");
        showWeatherNotification();
        return START_STICKY;
    }

    private void showWeatherNotification() {
        String locationId = Location.getDefaultLocationId();
        weatherRepository.fetchCurrentWeather(locationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                float temperature =  currentWeather.getTemperature();
                String locationName = Location.getDefaultLocationName(); // Get the location name

                // Create a remote view for the notification layout
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);

                // Fetch today's weather condition
                weatherRepository.fetchWeatherForecast(locationId, new WeatherRepository.WeatherCallback<List<Forecast>>() {
                    @Override
                    public void onSuccess(List<Forecast> forecastList) {
                        String todayWeatherCondition = weatherRepository.fetchTodayWeatherCondition(forecastList);
                        remoteViews.setTextViewText(R.id.condition_text_view, todayWeatherCondition);
                        int weatherIconResId = WeatherIconUtils.getWeatherIconResId(todayWeatherCondition, temperature);
                        // Get the current timestamp for the last refresh time
                        String lastRefreshTime = DateUtils.getCurrentTime();
                        String temperatureString = String.format(Locale.getDefault(), "%.0f°C", temperature);

                        remoteViews.setTextViewText(R.id.app_name_text_view, getString(R.string.app_name)); // Set the app name
                        remoteViews.setTextViewText(R.id.temperature_text_view, temperatureString);
                        remoteViews.setTextViewText(R.id.condition_text_view, todayWeatherCondition);
                        remoteViews.setTextViewText(R.id.location_text_view, locationName);
                        remoteViews.setImageViewResource(R.id.weather_icon_image_view, weatherIconResId);
                        remoteViews.setTextViewText(R.id.last_refresh_time_text_view, lastRefreshTime); // Set the last refresh time

                        // Create a pending intent for the refresh action
                        Intent refreshIntent = new Intent(getApplicationContext(), WeatherNotificationService.class);
                        refreshIntent.setAction("REFRESH_WEATHER");
                        PendingIntent refreshPendingIntent = PendingIntent.getService(getApplicationContext(), 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        remoteViews.setOnClickPendingIntent(R.id.refresh_image_view, refreshPendingIntent);

                        // Create a pending intent for the notification click
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                        // Build the notification with the custom layout
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                .setSmallIcon(R.drawable.tornado)
                                .setCustomContentView(remoteViews)
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true);

                        // Show the notification
                        startForeground(NOTIFICATION_ID, builder.build());
                    }

                    @Override
                    public void onFailure(String message) {
                        // Handle the failure scenario
                    }
                });
            }

            @Override
            public void onFailure(String message) {
                // Handle the failure scenario
            }
        });
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Weather Notifications";
            String description = "Shows current weather information";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("WeatherNotificationSer", "Service stopped");
        super.onDestroy();
        stopForeground(true);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}