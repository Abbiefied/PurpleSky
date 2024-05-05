package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.DateUtils;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.List;
import java.util.Locale;

public class WeatherWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Set click listener for the refresh icon
        Intent refreshIntent = new Intent(context, WeatherWidgetProvider.class);
        refreshIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.refresh_icon, refreshPendingIntent);

        // Update the widget with weather data
        WeatherRepository weatherRepository = new WeatherRepository(context);
        String locationId = Location.getDefaultLocationId();
        weatherRepository.fetchWeatherForecast(locationId, new WeatherRepository.WeatherCallback<List<Forecast>> () {
            @Override
            public void onSuccess(List<Forecast> forecastList) {
                Forecast today = forecastList.get(0);
                String windString = String.format(Locale.getDefault(), "%.0fmph", today.getWindSpeed());

                views.setImageViewResource(R.id.weather_icon, WeatherIconUtils.getWeatherIconResId(today.getWeatherCondition(), today.getMaxTemperatureCelcius()));
                views.setTextViewText(R.id.temperature, String.format(Locale.getDefault(), "%.0f°C", today.getMaxTemperatureCelcius()));
                views.setTextViewText(R.id.description, today.getWeatherCondition());
                views.setTextViewText(R.id.last_update_time, DateUtils.getCurrentTime());
                views.setTextViewText(R.id.wind_value, windString);
                views.setTextViewText(R.id.humidity_value, today.getHumidity());
                views.setTextViewText(R.id.pressure_value, today.getPressure());
                views.setTextViewText(R.id.location_name, Location.getLocationNameById(locationId));

                // Create an Intent to launch the app
                Intent appIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                if (appIntent != null) {
                    appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    views.setOnClickPendingIntent(R.id.widget_root_layout, pendingIntent);
                }

                appWidgetManager.updateAppWidget(appWidgetId, views);
            }

            @Override
            public void onFailure(String message) {
                // Handle the failure scenario
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WeatherWidgetProvider.class));
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
        }
    }
}