package org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;

public class DetailedForecastFragment extends Fragment {

    private static final String ARG_FORECAST = "forecast";

    private TextView dateTextView;
    private TextView dayOfWeekTextView;
    private TextView temperatureTextView;
    private TextView windTextView;
    private TextView humidityTextView;
    private TextView visibilityTextView;
    private TextView pressureTextView;
    private TextView windSpeedTextView;
    private TextView weatherConditionTextView;
    private TextView tWeatherConditionTextView;

    public DetailedForecastFragment() {
        // Required empty public constructor
    }

    public static DetailedForecastFragment newInstance(Forecast forecast) {
        DetailedForecastFragment fragment = new DetailedForecastFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FORECAST, forecast);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_forecast, container, false);
        dateTextView = view.findViewById(R.id.date_text_view);
        dayOfWeekTextView = view.findViewById(R.id.day_text_view);
        temperatureTextView = view.findViewById(R.id.temperature_text_view);
        windTextView = view.findViewById(R.id.wind_text_view);
        tWeatherConditionTextView = view.findViewById(R.id.tWeather_text_view);
        windSpeedTextView = view.findViewById(R.id.wind_speed_text_view);
        humidityTextView = view.findViewById(R.id.humidity_text_view);
        visibilityTextView = view.findViewById(R.id.visibility_text_view);
        pressureTextView = view.findViewById(R.id.pressure_text_view);
        weatherConditionTextView = view.findViewById(R.id.weatherCon_text_view);
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
        }
    }

    private void displayForecastDetails(Forecast forecast) {
        dateTextView.setText(forecast.getDate());
        temperatureTextView.setText(String.format("%.1f°C - %.1f°C", forecast.getMinTemperatureCelcius(), forecast.getMaxTemperatureCelcius()));
        windTextView.setText(forecast.getWindDirection());
        windSpeedTextView.setText(String.format("%.1fmph", forecast.getWindSpeed()));
        Log.d(TAG, "ForecastXML WindSpeed:" + String.format("%.1fmph", forecast.getWindSpeed()));
        humidityTextView.setText(forecast.getHumidity());
        tWeatherConditionTextView.setText(forecast.getTodayWeatherCondition());
        Log.d(TAG, "ForecastXML humidity:" + forecast.getHumidity());
        visibilityTextView.setText(forecast.getVisibility());
        Log.d(TAG, "ForecastXML visibility:" + forecast.getVisibility());
        pressureTextView.setText(forecast.getPressure());
        Log.d(TAG, "ForecastXML pressure:" + forecast.getPressure());
        weatherConditionTextView.setText(forecast.getWeatherCondition());
        Log.d(TAG, "ForecastXML weathercondition:" + forecast.getWeatherCondition());
    }
}