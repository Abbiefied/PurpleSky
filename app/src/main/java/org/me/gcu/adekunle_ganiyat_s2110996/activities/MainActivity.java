//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.AutoRefreshReceiver;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.ForecastAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.LocationAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherViewModel;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.DateUtils;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnForecastClickListener {

    private BottomNavigationView bottomNavigationView;
    private ImageButton settingsIcon;
    private WeatherRepository weatherRepository;
    private RecyclerView locationRecyclerView;
    private RecyclerView forecastRecyclerView;
    private TextView greetingTextView;
    private TextView temperatureTextView;
    private TextView locationTextView;
    private TextView dateTextView;
    private TextView dayTextView;
    private WeatherViewModel weatherViewModel;
    private ForecastAdapter forecastAdapter;
    private ImageButton prevButton, nextButton;
    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView windDirectionTextView;
    private TextView visibilityTextView;
    private TextView weatherConTextView;
    private ImageView weatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherRepository = new WeatherRepository(getApplicationContext());
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        initViews();
        initViewModel();
        setupObservers();
        setupListeners();
        handleSelectedLocation();
        setupLocationCarousel();
        scheduleAutoRefresh();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedMenuItem = getIntent().getIntExtra("selectedMenuItem", R.id.navigation_home);
        bottomNavigationView.setSelectedItemId(selectedMenuItem);

        //Settings Icon to navigate to Settings Activity
        settingsIcon = findViewById(R.id.settings_icon);
        settingsIcon.setOnClickListener(v -> {
            String locationId = Location.getDefaultLocationId();
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("locationId", locationId);
            startActivity(intent);
        });

    }
    private void initViews() {
        locationRecyclerView = findViewById(R.id.location_recycler_view);
        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);
        greetingTextView = findViewById(R.id.greeting_text_view);
        dateTextView = findViewById(R.id.date_text_view);
        dayTextView = findViewById(R.id.day_text_view);
        temperatureTextView = findViewById(R.id.temperature_text_view);
        locationTextView = findViewById(R.id.location_text_view);
        humidityTextView = findViewById(R.id.humidity_value);
        pressureTextView = findViewById(R.id.pressure_value);
        windDirectionTextView = findViewById(R.id.wind_direction_text_view);
        visibilityTextView = findViewById(R.id.visibility_value);
        weatherConTextView = findViewById(R.id.weather_condition);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        weatherIcon = findViewById(R.id.weather_icon);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
    }

    private void initViewModel() {
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
    }

    private void setupObservers() {
        weatherViewModel.getCurrentWeather().observe(this, this::updateCurrentWeather);
        weatherViewModel.getWeatherForecast().observe(this, forecasts -> {
            CurrentWeather currentWeather = weatherViewModel.getCurrentWeather().getValue();
            if (currentWeather != null) {
                updateWeatherForecast(forecasts, currentWeather);
            }
        });
//        weatherViewModel.getErrorMessage().observe(this, this::handleError);
    }

    private void updateWeatherForecast(List<Forecast> forecasts, CurrentWeather currentWeather) {
        if (forecasts != null && !forecasts.isEmpty()) {
            forecastAdapter = new ForecastAdapter(forecasts, this);
            forecastRecyclerView.setAdapter(forecastAdapter);
            // Use the weather condition of the first forecast item
            String todayWeatherCondition = weatherRepository.fetchTodayWeatherCondition(forecasts);
            currentWeather.setWeatherCondition(todayWeatherCondition);
            weatherConTextView.setText(todayWeatherCondition);

            int weatherIconResId = WeatherIconUtils.getWeatherIconResId(todayWeatherCondition, currentWeather.getTemperature());
            weatherIcon.setImageResource(weatherIconResId);

            Log.d(TAG, "updateWeatherForecast: setweathercondition: " + currentWeather.getWeatherCondition());
        } else {
            Log.d("MainActivity", "No forecast data available");
        }
    }

    private void setupListeners() {
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        // Set the layout manager to a horizontal LinearLayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        forecastRecyclerView.setLayoutManager(layoutManager);
    }

    private void handleSelectedLocation() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selected_location")) {
            Location selectedLocation = intent.getParcelableExtra("selected_location");
            if (selectedLocation != null) {
                String locationName = selectedLocation.getName();
                Location.setDefaultLocationName(locationName);
                locationTextView.setText(locationName);
                weatherViewModel.fetchWeatherData(selectedLocation);
            }
        } else {
            // Fetch weather data for the default location
            Location defaultLocation = new Location("Glasgow");
            weatherViewModel.fetchWeatherData(defaultLocation);
            locationTextView.setText(defaultLocation.getName());
        }
    }

    private void updateCurrentWeather(CurrentWeather currentWeather) {
        if (currentWeather != null) {
            greetingTextView.setText(DateUtils.getGreeting());
            dateTextView.setText(DateUtils.getCurrentDate());
            Log.d(TAG, "updateCurrentWeather: date" + currentWeather.getDate());
            temperatureTextView.setText(String.format("%.1f°C", currentWeather.getTemperature()));
            humidityTextView.setText(currentWeather.getHumidity());
            pressureTextView.setText(currentWeather.getPressure());
            windDirectionTextView.setText(String.format("%1s: %s ",currentWeather.getWindDirection(), currentWeather.getWindSpeed()));
            visibilityTextView.setText(currentWeather.getVisibility());
            locationTextView.setText(Location.getDefaultLocationName());
        }
    }

    private void handleError(String errorMessage) {
        Log.e("MainActivity", errorMessage);
        // Handle error scenario
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            //No need to start a new activity
            return true;
        } else if (itemId == R.id.navigation_search) {
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            searchIntent.putExtra("selectedMenuItem", R.id.navigation_search);
            startActivity(searchIntent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_map) {
            Intent mapIntent = new Intent(MainActivity.this, MapActivity.class);
            mapIntent.putExtra("selectedMenuItem", R.id.navigation_map);
            startActivity(mapIntent);
            finish();
            return true;
        } else if (itemId == R.id.navigation_compare) {
            Intent compareIntent = new Intent(MainActivity.this, WeatherComparisonActivity.class);
            compareIntent.putExtra("selectedMenuItem", R.id.navigation_compare);
            startActivity(compareIntent);
            finish();
            return true;
        }
        return false;
    }

    private void setupLocationCarousel() {
        List<Location> locations = Location.getPopularLocations();
        weatherRepository = new WeatherRepository(getApplicationContext());
        LocationAdapter locationAdapter = new LocationAdapter(locations, this::onLocationClicked, weatherRepository);
        RecyclerView locationCarouselRecyclerView = findViewById(R.id.location_recycler_view);
        locationCarouselRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        locationCarouselRecyclerView.setAdapter(locationAdapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(locationCarouselRecyclerView);

        locationCarouselRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                updateButtonVisibility();
            }
        });

        prevButton.setOnClickListener(v -> locationCarouselRecyclerView.smoothScrollBy(-locationCarouselRecyclerView.getWidth(), 0));
        nextButton.setOnClickListener(v -> locationCarouselRecyclerView.smoothScrollBy(locationCarouselRecyclerView.getWidth(), 0));

        // Set up auto-scroll functionality
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int currentPosition = locationCarouselRecyclerView.computeHorizontalScrollOffset();
                int maxPosition = locationCarouselRecyclerView.computeHorizontalScrollRange() - locationCarouselRecyclerView.getWidth();
                int nextPosition = (currentPosition + locationCarouselRecyclerView.getWidth()) % (maxPosition + locationCarouselRecyclerView.getWidth());
                locationCarouselRecyclerView.smoothScrollBy(nextPosition - currentPosition, 0);
                handler.postDelayed(this, 10000);
            }
        };
        handler.postDelayed(runnable, 10000);
    }

    private void updateButtonVisibility() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) locationRecyclerView.getLayoutManager();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

        prevButton.setVisibility(firstVisibleItemPosition == 0 ? View.INVISIBLE : View.VISIBLE);
        nextButton.setVisibility(lastVisibleItemPosition == locationRecyclerView.getAdapter().getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
    }

    private void onLocationClicked(Location location) {
        Location.setDefaultLocationId(String.valueOf(location.getId()));
        Log.d("MainActivity", String.valueOf(location.getId()));
        weatherViewModel.fetchWeatherData(location);
    }

    @Override
    public void onForecastClick(Forecast forecast) {
        Intent intent = new Intent(MainActivity.this, DetailedForecastActivity.class);
      intent.putExtra("forecast", forecast);
       startActivity(intent);
    }

    //Schedule auto refresh
    private void scheduleAutoRefresh() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Set the refresh times (8 am and 10 pm)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(this, AutoRefreshReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Schedule the alarms
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        calendar.set(Calendar.HOUR_OF_DAY, 22);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            // If the current destination is the home screen, minimize the app
            moveTaskToBack(true);

        } else {
            super.onBackPressed();
        }
    }

}