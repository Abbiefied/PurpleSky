//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherViewModel;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {

    private List<Location> locations;
    private OnLocationClickListener onLocationClickListener;
    private WeatherViewModel weatherViewModel;
    private WeatherRepository weatherRepository;

    public LocationAdapter(List<Location> locations, OnLocationClickListener onLocationClickListener, WeatherViewModel weatherViewModel) {
        this.locations = locations;
        this.onLocationClickListener = onLocationClickListener;
        this.weatherViewModel = weatherViewModel;
    }

    public LocationAdapter(List<Location> locations, OnLocationClickListener onLocationClickListener, WeatherRepository weatherRepository) {
        this.locations = locations;
        this.onLocationClickListener = onLocationClickListener;
        this.weatherRepository = weatherRepository;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locations.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView locationNameTextView;
        private TextView weatherConTextView;
        private TextView temperatureTextView;
        private TextView pressureTextView;
        private TextView humidityTextView;
        private TextView windSpeedTextView;
        private ImageView weatherIcon;

        private Location location;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.location_name_text_view);
            weatherConTextView = itemView.findViewById(R.id.weather_condition);
            temperatureTextView = itemView.findViewById(R.id.temperature_text_view);
            pressureTextView = itemView.findViewById(R.id.pressure_text_view);
            humidityTextView = itemView.findViewById(R.id.humidity_text_view);
            windSpeedTextView = itemView.findViewById(R.id.wind_speed_text_view);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);

            itemView.setOnClickListener(this);
        }

        public void bind(Location location) {
            this.location = location;
            locationNameTextView.setText(location.getName());

            String locationId = location.getLocationIdByName(location.getName());
            if (weatherViewModel != null) {
                weatherViewModel.fetchWeatherData(location);
                weatherViewModel.getCurrentWeather().observe((LifecycleOwner) itemView.getContext(), currentWeather -> {
                    if (currentWeather != null) {
                        temperatureTextView.setText(String.format("%.1f°C", currentWeather.getTemperature()));
                        weatherConTextView.setText(currentWeather.getWeatherCondition());
                        pressureTextView.setText(currentWeather.getPressure());
                        humidityTextView.setText(currentWeather.getHumidity());
                        windSpeedTextView.setText(currentWeather.getWindSpeed());
                    }
                });
            } else if (weatherRepository != null) {
                weatherRepository.fetchCurrentWeather(locationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
                    @Override
                    public void onSuccess(CurrentWeather currentWeather) {
                        float temperature =  currentWeather.getTemperature();

                        temperatureTextView.setText(String.format("%.1f°C", currentWeather.getTemperature()));
                        weatherRepository.fetchWeatherForecast(locationId, new WeatherRepository.WeatherCallback<List<Forecast>>() {
                                    @Override
                                    public void onSuccess(List<Forecast> forecastList) {
                                        String todayWeatherCondition = weatherRepository.fetchTodayWeatherCondition(forecastList);
                                        weatherConTextView.setText(todayWeatherCondition);
                                        int weatherIconResId = WeatherIconUtils.getWeatherIconResId(todayWeatherCondition, temperature);
                                        weatherIcon.setImageResource(weatherIconResId);
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        // Handle failure case
                                    }
                                });

                        humidityTextView.setText(currentWeather.getHumidity());
                        windSpeedTextView.setText(currentWeather.getWindSpeed());
                        pressureTextView.setText(currentWeather.getPressure());
                    }

                    @Override
                    public void onFailure(String message) {
                        // Handle failure case
                    }
                });
            }
        }

        @Override
        public void onClick(View v) {
            if (onLocationClickListener != null) {
                onLocationClickListener.onLocationClick(location);
            }
        }
    }

    public interface OnLocationClickListener {
        void onLocationClick(Location location);
    }
}