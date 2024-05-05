package org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.AppExecutors;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.List;

public class RecentLocationAdapter extends RecyclerView.Adapter<RecentLocationAdapter.RecentLocationViewHolder> {
    private List<Location> locations;
    private OnRecentLocationClickListener onRecentLocationClickListener;
    private WeatherRepository weatherRepository;

    public RecentLocationAdapter(List<Location> locations, OnRecentLocationClickListener onRecentLocationClickListener, WeatherRepository weatherRepository) {
        this.locations = locations;
        this.onRecentLocationClickListener = onRecentLocationClickListener;
        this.weatherRepository = weatherRepository;
    }

    @NonNull
    @Override
    public RecentLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new RecentLocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecentLocationViewHolder holder, int position) {
        if (locations != null && !locations.isEmpty()) {
            Location location = locations.get(position);
            holder.bind(location);
            fetchCurrentWeather(location, holder);
        } else {
            // Handle the case when the recent locations list is empty
            holder.bind(null);
        }
    }

    @Override
    public int getItemCount() {
        return locations != null ? locations.size() : 0;
    }

    public void updateData(List<Location> newLocations) {
        locations.clear();
        locations.addAll(newLocations);
        notifyDataSetChanged();
    }

    private void fetchCurrentWeather(Location location, RecentLocationViewHolder holder) {
        String locationName = location.getName();
        String locationId = location.getLocationIdByName(locationName);
        AppExecutors.getInstance().networkIO().execute(() -> {
            weatherRepository.fetchCurrentWeather(locationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    float temperature = currentWeather.getTemperature();
                    // Fetch today's weather condition
                    weatherRepository.fetchWeatherForecast(locationId, new WeatherRepository.WeatherCallback<List<Forecast>>() {
                        @Override
                        public void onSuccess(List<Forecast> forecastList) {
                            String todayWeatherCondition = weatherRepository.fetchTodayWeatherCondition(forecastList);
                            holder.weatherConTextView.setText(todayWeatherCondition);
                            int iconResId = WeatherIconUtils.getWeatherIconResId(todayWeatherCondition, temperature);
                            holder.weatherIcon.setImageResource(iconResId);

                        }
                        @Override
                        public void onFailure(String errorMessage) {
                            // Handle failure scenario
                            Log.e("RecentLocationAdapter", "Error fetching today's weather condition: " + errorMessage);
                        }
                    });

                    AppExecutors.getInstance().mainThread().execute(() -> {
                            holder.temperatureTextView.setText(String.format("%.1f°C", currentWeather.getTemperature()));
                            holder.humidityTextView.setText(currentWeather.getHumidity());
                            holder.windSpeedTextView.setText(currentWeather.getWindSpeed());
                            holder.pressureTextView.setText(currentWeather.getPressure());
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Handle failure scenario
                    Log.e("RecentLocationAdapter", "Error fetching current weather: " + errorMessage);
                }
            });
        });
    }

    public class RecentLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView locationNameTextView;
        private TextView temperatureTextView;
        private TextView humidityTextView;
        private TextView weatherConTextView;
        private TextView windSpeedTextView;
        private TextView pressureTextView;
        private ImageView weatherIcon;
        private Location location;

        public RecentLocationViewHolder(@NonNull View itemView) {
            super(itemView);
            locationNameTextView = itemView.findViewById(R.id.location_name_text_view);
            temperatureTextView = itemView.findViewById(R.id.temperature_text_view);
            humidityTextView = itemView.findViewById(R.id.humidity_text_view);
            pressureTextView = itemView.findViewById(R.id.pressure_text_view);
            windSpeedTextView = itemView.findViewById(R.id.wind_speed_text_view);
            weatherConTextView =itemView.findViewById(R.id.weather_condition);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            itemView.setOnClickListener(this);
        }

        public void bind(Location location) {
            if (location != null) {
                this.location = location;
                    locationNameTextView.setText(location.getName());
                    temperatureTextView.setText("");
                    humidityTextView.setText("");
                    pressureTextView.setText("");
                    weatherConTextView.setText("");
                    windSpeedTextView.setText("");
                    weatherIcon.setImageDrawable(null);

            } else {
                // Handle the case when the location is null
                if (locationNameTextView != null) {
                    locationNameTextView.setText("");
                }
                if (temperatureTextView != null) {
                    temperatureTextView.setText("");
                }
                if (humidityTextView != null) {
                    humidityTextView.setText("");
                }
                if (pressureTextView != null) {
                    pressureTextView.setText("");
                }
                if (windSpeedTextView != null) {
                    windSpeedTextView.setText("");
                }
                if (weatherConTextView != null) {
                    weatherConTextView.setText("");
                }
                if (weatherIcon != null) {
                    weatherIcon.setImageDrawable(null);
                }
            }
        }

        @Override
        public void onClick(View v) {
            if (onRecentLocationClickListener != null && location != null) {
                onRecentLocationClickListener.onRecentLocationClick(location);
            }
        }
    }

    public interface OnRecentLocationClickListener {
        void onRecentLocationClick(Location location);
    }
}