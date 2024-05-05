//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.NetworkDataSource;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherComparisonViewModel;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.AppExecutors;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.DateUtils;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherComparisonActivity extends AppCompatActivity {
    private WeatherComparisonViewModel viewModel;
    private CurrentWeather currentWeather1;
    private CurrentWeather currentWeather2;
    private List<Forecast> weatherForecast1;
    private List<Forecast> weatherForecast2;
    private AirQualityData airQualityData1;
    private AirQualityData airQualityData2;
    private Spinner locationSpinner1;
    private Spinner locationSpinner2;
    private TextView locationNameTextView1;
    private TextView dayOfWeekTextView1;
    private TextView dateTextView1;
    private TextView locationNameTextView2;
    private TextView dayOfWeekTextView2;
    private TextView dateTextView2;
    private ImageView weatherIcon1;
    private ImageView weatherIcon2;
    private TextView temperatureTextView1;
    private TextView temperatureTextView2;
    private TextView humidityTextView1;
    private TextView humidityTextView2;
    private TextView windTextView1;
    private TextView windTextView2;
    private TextView airQualityTextView1;
    private TextView airQualityTextView2;
    private TextView weatherConTextView1;
    private TextView weatherConTextView2;
    private TextView comparisonSummaryTextView;
    private WeatherRepository weatherRepository;
    private String todayWeatherCondition1;
    private String todayWeatherCondition2;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_comparison);

        weatherRepository = new WeatherRepository(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewModel = new ViewModelProvider(this).get(WeatherComparisonViewModel.class);

        currentWeather1 = null;
        currentWeather2 = null;
        weatherForecast1 = null;
        weatherForecast2 = null;
        airQualityData1 = null;
        airQualityData2 = null;


        // Initialize UI components
        locationSpinner1 = findViewById(R.id.location_spinner_1);
        locationSpinner2 = findViewById(R.id.location_spinner_2);
        locationNameTextView1 = findViewById(R.id.location_name_text_view_1);
        dayOfWeekTextView1 = findViewById(R.id.day_of_week_text_view_1);
        dateTextView1 = findViewById(R.id.date_text_view_1);
        locationNameTextView2 = findViewById(R.id.location_name_text_view_2);
        dayOfWeekTextView2 = findViewById(R.id.day_of_week_text_view_2);
        dateTextView2 = findViewById(R.id.date_text_view_2);
        weatherIcon1 = findViewById(R.id.weather_icon_1);
        weatherIcon2 = findViewById(R.id.weather_icon_2);
        temperatureTextView1 = findViewById(R.id.temperature_text_view_1);
        temperatureTextView2 = findViewById(R.id.temperature_text_view_2);
        humidityTextView1 = findViewById(R.id.humidity_text_view_1);
        humidityTextView2 = findViewById(R.id.humidity_text_view_2);
        windTextView1 = findViewById(R.id.wind_text_view_1);
        windTextView2 = findViewById(R.id.wind_text_view_2);
        airQualityTextView1 = findViewById(R.id.air_quality_text_view_1);
        airQualityTextView2 = findViewById(R.id.air_quality_text_view_2);
        comparisonSummaryTextView = findViewById(R.id.comparison_summary_text_view);
        weatherConTextView1 = findViewById(R.id.weatherCon_text_view_1);
        weatherConTextView2 = findViewById(R.id.weatherCon_text_view_2);


        // Apply fade-in animation
        applyFadeInAnimation(weatherIcon1, weatherIcon2, temperatureTextView1, temperatureTextView2,
                humidityTextView1, humidityTextView2, windTextView1, windTextView2,
                airQualityTextView1, airQualityTextView2, comparisonSummaryTextView);


        // Set up location spinners
        List<Location> locations = Location.getPopularLocations();
        ArrayAdapter<Location> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner1.setAdapter(adapter);
        locationSpinner2.setAdapter(adapter);

        // Set initial values for the spinners
        Location glasgow = getLocationByName(locations, "Glasgow");
        Location mauritius = getLocationByName(locations, "Mauritius");

        if (glasgow != null) {
            int glasgowPosition = locations.indexOf(glasgow);
            locationSpinner1.setSelection(glasgowPosition);
        }

        if (mauritius != null) {
            int mauritiusPosition = locations.indexOf(mauritius);
            locationSpinner2.setSelection(mauritiusPosition);
        }

        // Set up spinner selection listeners
        locationSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Location selectedLocation1 = (Location) parent.getItemAtPosition(position);
                Location selectedLocation2 = (Location) locationSpinner2.getSelectedItem();
                fetchWeatherData(selectedLocation1, selectedLocation2);

                // Set location name, day of the week, and date for location 1
                locationNameTextView1.setText(selectedLocation1.getName());
                dayOfWeekTextView1.setText(DateUtils.getDayOfWeek());
                dateTextView1.setText(DateUtils.getCurrentDate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        locationSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Location selectedLocation1 = (Location) locationSpinner1.getSelectedItem();
                Location selectedLocation2 = (Location) parent.getItemAtPosition(position);
                fetchWeatherData(selectedLocation1, selectedLocation2);

                locationNameTextView2.setText(selectedLocation2.getName());
                dayOfWeekTextView2.setText(DateUtils.getDayOfWeek());
                dateTextView2.setText(DateUtils.getCurrentDate());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        viewModel.getWeatherForecast1().observe(this, forecastList -> {
            if (forecastList !=null) {
                weatherForecast1 = forecastList;
                Forecast today1 = weatherForecast1.get(0);
                float temperature1 = today1.getMaxTemperatureCelcius();

                todayWeatherCondition1 = weatherRepository.fetchTodayWeatherCondition(forecastList);
                weatherConTextView1.setText(todayWeatherCondition1);
                int weatherIconResId = WeatherIconUtils.getWeatherIconResId(todayWeatherCondition1, temperature1);
                weatherIcon1.setImageResource(weatherIconResId);
            }
        });

        viewModel.getWeatherForecast2().observe(this, forecastList -> {
            if (forecastList !=null) {
                weatherForecast2 = forecastList;
                Forecast today2 = weatherForecast1.get(0);
                float temperature2 = today2.getMaxTemperatureCelcius();

                todayWeatherCondition2 = weatherRepository.fetchTodayWeatherCondition(forecastList);
                weatherConTextView2.setText(todayWeatherCondition2);
                int weatherIconResId = WeatherIconUtils.getWeatherIconResId(todayWeatherCondition2, temperature2);
                weatherIcon2.setImageResource(weatherIconResId);
            }
        });

        // Observe LiveData objects and update UI
        viewModel.getCurrentWeather1().observe(this, currentWeather -> {
            if (currentWeather != null) {
                currentWeather1 = currentWeather;
                temperatureTextView1.setText(String.format(Locale.getDefault(), "%.1f°C", currentWeather.getTemperature()));
                humidityTextView1.setText(String.format(Locale.getDefault(), "Humidity: ", currentWeather.getHumidity()));
                windTextView1.setText(String.format(Locale.getDefault(), "Wind: %s", currentWeather.getWindSpeed()));
                checkAndGenerateComparisonSummary();
            }
        });

        viewModel.getCurrentWeather2().observe(this, currentWeather -> {
            if (currentWeather != null) {
                currentWeather2 = currentWeather;
                temperatureTextView2.setText(String.format(Locale.getDefault(), "%.1f°C", currentWeather.getTemperature()));
                humidityTextView2.setText(String.format(Locale.getDefault(), "Humidity: ", currentWeather.getHumidity()));
                windTextView2.setText(String.format(Locale.getDefault(), "Wind: %s", currentWeather.getWindSpeed()));
                checkAndGenerateComparisonSummary();
            }
        });

        viewModel.getAirQualityData1().observe(this, airQualityData -> {
            if (airQualityData != null) {
                airQualityData1 = airQualityData;
                airQualityTextView1.setText(formatAirQualityData(airQualityData1));
                checkAndGenerateComparisonSummary();
            }
        });

        viewModel.getAirQualityData2().observe(this, airQualityData -> {
            if (airQualityData != null) {
                airQualityData2 = airQualityData;
                airQualityTextView2.setText(formatAirQualityData(airQualityData2));
                checkAndGenerateComparisonSummary();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedMenuItem = getIntent().getIntExtra("selectedMenuItem", R.id.navigation_compare);
        bottomNavigationView.setSelectedItemId(selectedMenuItem);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    Intent homeIntent = new Intent(WeatherComparisonActivity.this, MainActivity.class);
                    homeIntent.putExtra("selectedMenuItem", R.id.navigation_home);
                    startActivity(homeIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    Intent searchIntent = new Intent(WeatherComparisonActivity.this, MapActivity.class);
                    searchIntent.putExtra("selectedMenuItem", R.id.navigation_map);
                    startActivity(searchIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    Intent mapIntent = new Intent(WeatherComparisonActivity.this, MapActivity.class);
                    mapIntent.putExtra("selectedMenuItem", R.id.navigation_map);
                    startActivity(mapIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_compare) {
                    //No need to start a new activity
                    return true;
                }
                return false;
            }
        });

    }

    private Location getLocationByName(List<Location> locations, String name) {
        for (Location location : locations) {
            if (location.getName().equals(name)) {
                return location;
            }
        }
        return null;
    }
    private void checkAndGenerateComparisonSummary() {
        if (currentWeather1 != null && currentWeather2 != null &&
                weatherForecast1 != null && weatherForecast2 != null &&
                airQualityData1 != null && airQualityData2 != null) {
            generateComparisonSummary(currentWeather1, currentWeather2, weatherForecast1, weatherForecast2, airQualityData1, airQualityData2);
        }
    }

    private void fetchWeatherData(Location location1, Location location2) {
        viewModel.fetchWeatherData(location1, location2);
    }


    private String formatAirQualityData(AirQualityData airQualityData) {
        // Format the air quality data for display
        StringBuilder builder = new StringBuilder();
        builder.append("Air Quality Index: ").append(airQualityData.getAirQualityIndex()).append("\n");
        builder.append("Dominant Pollutant: ").append(airQualityData.getIndexes().get(0).getDominantPollutant()).append("\n");
        builder.append("Category: ").append(airQualityData.getIndexes().get(0).getCategory()).append("\n");
        return builder.toString();
    }

    private void generateComparisonSummary(CurrentWeather currentWeather1, CurrentWeather currentWeather2,
                                           List<Forecast> weatherForecast1, List<Forecast> weatherForecast2,
                                           AirQualityData airQualityData1, AirQualityData airQualityData2) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            StringBuilder summaryBuilder = new StringBuilder();

            // Compare current temperature
            if (currentWeather1 != null && currentWeather2 != null) {
                float temperature1 = currentWeather1.getTemperature();
                float temperature2 = currentWeather2.getTemperature();
                String location1 = locationSpinner1.getSelectedItem().toString();
                String location2 = locationSpinner2.getSelectedItem().toString();

                if (temperature1 > temperature2) {
                    float temperatureDifference = temperature1 - temperature2;
                    if (temperatureDifference > 10) {
                        summaryBuilder.append(location1).append(" is significantly warmer than ").append(location2).append(" today. ");
                    } else {
                        summaryBuilder.append(location1).append(" is warmer than ").append(location2).append(" today. ");
                    }
                } else if (temperature1 < temperature2) {
                    float temperatureDifference = temperature2 - temperature1;
                    if (temperatureDifference > 10) {
                        summaryBuilder.append(location2).append(" is significantly warmer than ").append(location1).append(" today. ");
                    } else {
                        summaryBuilder.append(location2).append(" is warmer than ").append(location1).append(" today. ");
                    }
                } else {
                    summaryBuilder.append("Both ").append(location1).append(" and ").append(location2).append(" have the same temperature today. ");
                }

                if (temperature1 < 0 || temperature2 < 0) {
                    summaryBuilder.append("It is extremely cold in ");
                    if (temperature1 < 0 && temperature2 < 0) {
                        summaryBuilder.append("both ").append(location1).append(" and ").append(location2).append(". ");
                    } else if (temperature1 < 0) {
                        summaryBuilder.append(location1).append(". ");
                    } else {
                        summaryBuilder.append(location2).append(". ");
                    }
                } else if (temperature1 > 35 || temperature2 > 35) {
                    summaryBuilder.append("It is extremely hot in ");
                    if (temperature1 > 35 && temperature2 > 35) {
                        summaryBuilder.append("both ").append(location1).append(" and ").append(location2).append(". ");
                    } else if (temperature1 > 35) {
                        summaryBuilder.append(location1).append(". ");
                    } else {
                        summaryBuilder.append(location2).append(". ");
                    }
                }
            }

            // Compare air quality index
            if (airQualityData1 != null && airQualityData2 != null) {
                int aqiValue1 = Integer.parseInt(airQualityData1.getAirQualityIndex());
                int aqiValue2 = Integer.parseInt(airQualityData2.getAirQualityIndex());
                String location1 = locationSpinner1.getSelectedItem().toString();
                String location2 = locationSpinner2.getSelectedItem().toString();

                if (aqiValue1 >= 50 && aqiValue2 >= 50) {
                    summaryBuilder.append("The air quality is good in both ").append(location1).append(" and ").append(location2).append(". ");
                } else if (aqiValue1 >= 50) {
                    summaryBuilder.append("The air quality is good in ").append(location1).append(", but it is relatively poor in ").append(location2).append(". ");
                } else if (aqiValue2 >= 50) {
                    summaryBuilder.append("The air quality is good in ").append(location2).append(", but it is relatively poor in ").append(location1).append(". ");
                } else {
                    summaryBuilder.append("The air quality is relatively poor in both ").append(location1).append(" and ").append(location2).append(". ");
                }
            }

            // Compare dominant pollutants
            if (airQualityData1 != null && airQualityData2 != null && !airQualityData1.getIndexes().isEmpty() && !airQualityData2.getIndexes().isEmpty()) {
                String dominantPollutant1 = airQualityData1.getIndexes().get(0).getDominantPollutant();
                String dominantPollutant2 = airQualityData2.getIndexes().get(0).getDominantPollutant();
                String location1 = locationSpinner1.getSelectedItem().toString();
                String location2 = locationSpinner2.getSelectedItem().toString();

                if (dominantPollutant1 != null && dominantPollutant2 != null) {
                    summaryBuilder.append("The dominant pollutant in ").append(location1).append(" is ").append(dominantPollutant1)
                            .append(", while it is ").append(dominantPollutant2).append(" in ").append(location2).append(". ");
                }
            }

            // Compare forecast weather
            if (weatherForecast1 != null && weatherForecast2 != null && !weatherForecast1.isEmpty() && !weatherForecast2.isEmpty()) {
                String weatherCondition1 = weatherRepository.fetchTodayWeatherCondition(weatherForecast1);
                String weatherCondition2 = weatherRepository.fetchTodayWeatherCondition(weatherForecast2);
                float maxTemperature1 = weatherForecast1.get(0).getMaxTemperatureCelcius();
                float maxTemperature2 = weatherForecast2.get(0).getMaxTemperatureCelcius();
                String location1 = locationSpinner1.getSelectedItem().toString();
                String location2 = locationSpinner2.getSelectedItem().toString();

                summaryBuilder.append("The weather forecast for ").append(location1).append(" is ").append(weatherCondition1)
                        .append(" with a maximum temperature of ").append(maxTemperature1).append("°C, ")
                        .append("while it is ").append(weatherCondition2).append(" with a maximum temperature of ")
                        .append(maxTemperature2).append("°C for ").append(location2).append(". ");
            }

            String comparisonSummary = summaryBuilder.toString();

            AppExecutors.getInstance().mainThread().execute(() -> {
                comparisonSummaryTextView.setText(comparisonSummary);
            });
        });
    }

    private void applyFadeInAnimation(View... views) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        for (View view : views) {
            view.startAnimation(fadeInAnimation);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}