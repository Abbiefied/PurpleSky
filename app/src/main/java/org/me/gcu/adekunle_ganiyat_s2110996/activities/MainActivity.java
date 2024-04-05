package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.ForecastAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.LocationAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SearchViewModel;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherViewModel;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private WeatherRepository weatherRepository;
    private RecyclerView locationRecyclerView;
    private RecyclerView forecastRecyclerView;
    private TextView greetingTextView;
    private TextView temperatureTextView;
    private TextView locationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherRepository = new WeatherRepository(getApplicationContext());

        initViews();
        setupLocationCarousel();
        fetchAndDisplayCurrentWeather();
        fetchAndDisplayWeatherForecast();


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = (item.getItemId());
                if (itemId == R.id.navigation_home) {
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
    private void initViews() {
        locationRecyclerView = findViewById(R.id.location_recycler_view);
        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);
        greetingTextView = findViewById(R.id.greeting_text_view);
        temperatureTextView = findViewById(R.id.temperature_text_view);
        locationTextView = findViewById(R.id.location_text_view);
    }

    private void setupLocationCarousel() {
        List<Location> locations = Location.getPopularLocations();
        LocationAdapter locationAdapter = new LocationAdapter(locations, this::onLocationClicked);
        locationRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        locationRecyclerView.setAdapter(locationAdapter);
    }

    private void onLocationClicked(Location location) {
        Location.setDefaultLocationId(String.valueOf(location.getId()));
        Log.d("MainActivity", String.valueOf(location.getId()));
        fetchAndDisplayCurrentWeather();
        fetchAndDisplayWeatherForecast();
    }

    private void fetchAndDisplayCurrentWeather() {
        String defaultLocationId = Location.getDefaultLocationId();
        weatherRepository.fetchCurrentWeather(defaultLocationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
                displayCurrentWeather(currentWeather);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure scenario
            }
        });
    }

    private void displayCurrentWeather(CurrentWeather currentWeather) {
        greetingTextView.setText(DateUtils.getGreeting());
        temperatureTextView.setText(String.format("%.1f°C", currentWeather.getTemperature()));
        locationTextView.setText(Location.getDefaultLocationName());
    }

    private void fetchAndDisplayWeatherForecast() {
        String defaultLocationId = Location.getDefaultLocationId();
        weatherRepository.fetchWeatherForecast(defaultLocationId, new WeatherRepository.WeatherCallback<List<Forecast>>() {
            @Override
            public void onSuccess(List<Forecast> forecastList) {
                displayWeatherForecast(forecastList);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure scenario
            }
        });
    }
    private void displayWeatherForecast(List<Forecast> forecastList) {
        if (forecastList != null && !forecastList.isEmpty()) {
            ForecastAdapter forecastAdapter = new ForecastAdapter(forecastList, this::onForecastClicked);
            forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            forecastRecyclerView.setAdapter(forecastAdapter);
        } else {
            Log.d("MainActivity", "No forecast data available");
            // Handle the case when forecast data is missing, e.g., show an error message or placeholder
        }
    }

    private void onForecastClicked(Forecast forecast) {
        Intent intent = new Intent(MainActivity.this, DetailedForecastActivity.class);
        intent.putExtra("forecast", forecast);
        startActivity(intent);
    }
}