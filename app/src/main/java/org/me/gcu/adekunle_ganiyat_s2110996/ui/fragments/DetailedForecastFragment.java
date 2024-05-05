package org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

public class DetailedForecastFragment extends Fragment {

    private static final String ARG_FORECAST = "forecast";
    private static final String ARG_LOCATION_NAME = "locationName";

    private TextView dateTextView;
    private TextView dayOfWeekTextView;
    private TextView maxTempTextView;
    private TextView windTextView;
    private TextView humidityTextView;
    private TextView visibilityTextView;
    private TextView pressureTextView;
    private TextView windSpeedTextView;
    private TextView weatherConditionTextView;
    private TextView minTempTextView;
    private TextView uvTextView;
    private TextView pollutionTextView;
    private TextView locationTextView;
    private ImageView weatherIcon;

    public DetailedForecastFragment() {
        // Default constructor
    }

    public static DetailedForecastFragment newInstance(Forecast forecast, String locationName) {
        DetailedForecastFragment fragment = new DetailedForecastFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FORECAST, forecast);
        args.putString(ARG_LOCATION_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_forecast, container, false);
        dateTextView = view.findViewById(R.id.date_text_view);
        dayOfWeekTextView = view.findViewById(R.id.day_text_view);
        maxTempTextView = view.findViewById(R.id.max_temp_text_view);
        windTextView = view.findViewById(R.id.wind_text_view);
        windSpeedTextView = view.findViewById(R.id.wind_speed_text_view);
        humidityTextView = view.findViewById(R.id.humidity_text_view);
        visibilityTextView = view.findViewById(R.id.visibility_text_view);
        pressureTextView = view.findViewById(R.id.pressure_text_view);
        weatherConditionTextView = view.findViewById(R.id.weatherCon_text_view);
        minTempTextView = view.findViewById(R.id.min_temp_text_view);
        uvTextView = view.findViewById(R.id.uv_text_view);
        pollutionTextView = view.findViewById(R.id.pollution_text_view);
        locationTextView = view.findViewById(R.id.location_text_view);
        weatherIcon = view.findViewById(R.id.weather_icon);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            Forecast forecast = getArguments().getParcelable(ARG_FORECAST);
            if (forecast != null) {
                displayForecastDetails(forecast);
            }

            String locationName = getArguments().getString(ARG_LOCATION_NAME);
            if (locationName != null) {
                locationTextView.setText(locationName);
            }
        }
    }

    private void displayForecastDetails(Forecast forecast) {
        dateTextView.setText(forecast.getDate());
        dayOfWeekTextView.setText(forecast.getDayOfWeek());
        minTempTextView.setText(String.format("%.1f°C", forecast.getMinTemperatureCelcius()));
        maxTempTextView.setText(String.format("%.1f°C", forecast.getMaxTemperatureCelcius()));
        windTextView.setText(forecast.getWindDirection());
        windSpeedTextView.setText(String.format("%.1fmph", forecast.getWindSpeed()));
        humidityTextView.setText(forecast.getHumidity());
        visibilityTextView.setText(forecast.getVisibility());
        pressureTextView.setText(forecast.getPressure());
        weatherConditionTextView.setText(forecast.getWeatherCondition());
        uvTextView.setText(forecast.getUvRisk());
        pollutionTextView.setText(forecast.getPollution());
        int weatherIconResId = WeatherIconUtils.getWeatherIconResId(forecast.getWeatherCondition(), forecast.getMaxTemperatureCelcius());
        weatherIcon.setImageResource(weatherIconResId);


    }
}