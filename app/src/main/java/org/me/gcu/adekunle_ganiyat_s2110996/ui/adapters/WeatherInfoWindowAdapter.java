package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;

import java.util.Map;

public class WeatherInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private final Context mContext;
    private final Map<String, CurrentWeather> mWeatherData;

    public WeatherInfoWindowAdapter(Context context, Map<String, CurrentWeather> weatherData) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.layout_info_window, null);
        mWeatherData = weatherData;
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
        String locationId = (String) marker.getTag();
        CurrentWeather currentWeather = mWeatherData.get(locationId);

        if (currentWeather != null) {
            TextView tvLocationName = view.findViewById(R.id.tv_location_name);
            TextView tvTemperature = view.findViewById(R.id.tv_temperature);
            TextView tvHumidity = view.findViewById(R.id.tv_humidity);
            TextView tvWindSpeed = view.findViewById(R.id.tv_wind_speed);

            tvLocationName.setText(marker.getTitle());
            tvTemperature.setText("Temperature: " + currentWeather.getTemperature() + "°C");
            tvHumidity.setText("Humidity: " + currentWeather.getHumidity());
            tvWindSpeed.setText("Wind Speed: " + currentWeather.getWindSpeed());
        }
    }
}
