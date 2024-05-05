package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.CustomMarker;

import java.util.Map;

public class WeatherInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final Context mContext;

    public WeatherInfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.layout_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void render(Marker marker, View view) {
        Object tag = marker.getTag();
        if (tag instanceof CustomMarker) {
            CustomMarker customMarker = (CustomMarker) tag;
            if (customMarker.isWeatherMarker()) {
                CurrentWeather currentWeather = customMarker.getCurrentWeather();

                TextView tvWeatherCondition = view.findViewById(R.id.tv_weather_condition);
                TextView tvTemperature = view.findViewById(R.id.tv_temperature);
                TextView tvHumidity = view.findViewById(R.id.tv_humidity);
                TextView tvWindSpeed = view.findViewById(R.id.tv_wind_speed);
                TextView tvDayOfWeek = view.findViewById(R.id.tv_day_of_week);
                TextView tvPressure = view.findViewById(R.id.tv_pressure);
                TextView tvVisibility = view.findViewById(R.id.tv_visibility);

                tvWeatherCondition.setText(currentWeather.getWeatherCondition());
                tvDayOfWeek.setText(currentWeather.getDate());
                tvTemperature.setText("Temperature: " + currentWeather.getTemperature() + "°C");
                tvHumidity.setText("Humidity: " + currentWeather.getHumidity());
                tvWindSpeed.setText("Wind Speed: " + currentWeather.getWindSpeed());
                tvPressure.setText("Pressure: " + currentWeather.getPressure());
                tvVisibility.setText("Visibility: " + currentWeather.getVisibility());
            }
        }
    }
}