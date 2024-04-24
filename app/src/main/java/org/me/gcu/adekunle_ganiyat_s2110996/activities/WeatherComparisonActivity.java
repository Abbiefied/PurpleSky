package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
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
    private TextView forecastTextView1;
    private TextView forecastTextView2;
    private TextView comparisonSummaryTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_comparison);

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
//        forecastTextView1 = findViewById(R.id.forecast_text_view_1);
//        forecastTextView2 = findViewById(R.id.forecast_text_view_2);


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

        // Observe LiveData objects and update UI
        viewModel.getCurrentWeather1().observe(this, currentWeather -> {
            if (currentWeather != null) {
                currentWeather1 = currentWeather;
                weatherIcon1.setImageResource(WeatherIconUtils.getWeatherIconResId(currentWeather.getTemperature()));
                temperatureTextView1.setText(String.format(Locale.getDefault(), "%.1f°C", currentWeather.getTemperature()));
                humidityTextView1.setText(String.format(Locale.getDefault(), "Humidity: %s%%", currentWeather.getHumidity()));
                windTextView1.setText(String.format(Locale.getDefault(), "Wind: %s", currentWeather.getWindSpeed()));
                checkAndGenerateComparisonSummary();
            }
        });

        viewModel.getCurrentWeather2().observe(this, currentWeather -> {
            if (currentWeather != null) {
                currentWeather2 = currentWeather;
                weatherIcon2.setImageResource(WeatherIconUtils.getWeatherIconResId(currentWeather.getTemperature()));
                temperatureTextView2.setText(String.format(Locale.getDefault(), "%.1f°C", currentWeather.getTemperature()));
                humidityTextView2.setText(String.format(Locale.getDefault(), "Humidity: %s%%", currentWeather.getHumidity()));
                windTextView2.setText(String.format(Locale.getDefault(), "Wind: %s", currentWeather.getWindSpeed()));
                checkAndGenerateComparisonSummary();
            }
        });

        viewModel.getAirQualityData1().observe(this, airQualityData -> {
            if (airQualityData != null) {
                airQualityData1 = airQualityData;
                airQualityTextView1.setText(String.format(Locale.getDefault(), "AQI: %s", airQualityData.getAirQualityIndex()));
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

//
//        viewModel.getWeatherForecast1().observe(this, weatherForecast -> {
//            if (weatherForecast != null) {
//                weatherForecast1 = weatherForecast;
//                forecastTextView1.setText(formatWeatherForecast(weatherForecast));
//            }
//        });
//
//        viewModel.getWeatherForecast2().observe(this, weatherForecast -> {
//            if (weatherForecast != null) {
//                weatherForecast2 = weatherForecast;
//                forecastTextView2.setText(formatWeatherForecast(weatherForecast));
//            }
//        });


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
//                weatherForecast1 != null && weatherForecast2 != null &&
                airQualityData1 != null && airQualityData2 != null) {
            generateComparisonSummary(currentWeather1, currentWeather2, weatherForecast1, weatherForecast2, airQualityData1, airQualityData2);
        }
    }

    private void fetchWeatherData(Location location1, Location location2) {
        viewModel.fetchWeatherData(location1, location2);
    }

    private String formatCurrentWeather(CurrentWeather currentWeather) {
        // Format the current weather data for display
        StringBuilder builder = new StringBuilder();
        builder.append("Temperature: ").append(currentWeather.getTemperature()).append("\n");
        builder.append("Humidity: ").append(currentWeather.getHumidity()).append("\n");
        builder.append("Wind Speed: ").append(currentWeather.getWindSpeed()).append("\n");
        builder.append("Pressure: ").append(currentWeather.getPressure()).append("\n");
        return builder.toString();
    }

//    private String formatWeatherForecast(List<Forecast> weatherForecast) {
//        // Format the weather forecast data for display
//        StringBuilder builder = new StringBuilder();
//        for (Forecast forecast : weatherForecast) {
//            builder.append("Date: ").append(forecast.getDate()).append("\n");
//            builder.append("Temperature: ").append(forecast.getMaxTemperatureCelcius()).append("°C / ")
//                    .append(forecast.getMaxTemperatureFahrenheit()).append("°F\n");
//            builder.append("Weather Condition: ").append(forecast.getWeatherCondition()).append("\n");
//            builder.append("Wind Speed: ").append(forecast.getWindSpeed()).append(" mph\n");
//            builder.append("Pressure: ").append(forecast.getPressure()).append(" mb\n");
//            builder.append("Humidity: ").append(forecast.getHumidity()).append("%\n");
//            builder.append("\n");
//        }
//        return builder.toString();
//    }

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
                String weatherCondition1 = weatherForecast1.get(0).getWeatherCondition();
                String weatherCondition2 = weatherForecast2.get(0).getWeatherCondition();
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
}