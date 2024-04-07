package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.ForecastAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.LocationAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherViewModel;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.DateUtils;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.OnForecastClickListener {

    private BottomNavigationView bottomNavigationView;

    private WeatherRepository weatherRepository;
    private RecyclerView locationRecyclerView;
    private RecyclerView forecastRecyclerView;
    private TextView greetingTextView;
    private TextView temperatureTextView;
    private TextView locationTextView;
    private WeatherViewModel weatherViewModel;
    private ForecastAdapter forecastAdapter;
    private ImageButton prevButton, nextButton;

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


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Log.d("MainActivity", "bottomNavigationView: " + bottomNavigationView);
    }
    private void initViews() {
        locationRecyclerView = findViewById(R.id.location_recycler_view);
        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);
        greetingTextView = findViewById(R.id.greeting_text_view);
        temperatureTextView = findViewById(R.id.temperature_text_view);
        locationTextView = findViewById(R.id.location_text_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
    }

    private void initViewModel() {
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
    }

    private void setupObservers() {
        weatherViewModel.getCurrentWeather().observe(this, this::updateCurrentWeather);
        weatherViewModel.getWeatherForecast().observe(this, this::updateWeatherForecast);
//        weatherViewModel.getErrorMessage().observe(this, this::handleError);
    }

    private void setupListeners() {
        bottomNavigationView.setOnItemSelectedListener(this::onNavigationItemSelected);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
            temperatureTextView.setText(String.format("%.1f°C", currentWeather.getTemperature()));
            locationTextView.setText(Location.getDefaultLocationName());
        }
    }

    private void updateWeatherForecast(List<Forecast> forecasts) {
        if (forecasts != null && !forecasts.isEmpty()) {
            forecastAdapter = new ForecastAdapter(forecasts, this);
            forecastRecyclerView.setAdapter(forecastAdapter);
        } else {
            Log.d("MainActivity", "No forecast data available");
        }
    }

    private void handleError(String errorMessage) {
        Log.e("MainActivity", errorMessage);
        // Handle error scenario
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            // Handle home item click
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

    private void setupLocationCarousel() {
        List<Location> locations = Location.getPopularLocations();
        LocationAdapter locationAdapter = new LocationAdapter(locations, this::onLocationClicked);
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
                handler.postDelayed(this, 10000); // Adjust the delay as needed (3 seconds in this case)
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

    @Override
    public void onBackPressed() {
        if (bottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            // If the current destination is the home screen, minimize the app or finish the activity
            moveTaskToBack(true);
            // or
            // finish();
        } else {
            super.onBackPressed();
        }
    }

}