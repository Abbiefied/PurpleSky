package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments.DetailedForecastFragment;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherViewModel;

public class DetailedForecastActivity extends AppCompatActivity {

    private WeatherViewModel weatherViewModel;
    private BottomNavigationView bottomNavigationView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_forecast);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        if (getIntent() != null && getIntent().hasExtra("forecast")) {
            Forecast forecast = getIntent().getParcelableExtra("forecast");
            if (forecast != null) {
                showDetailedForecastFragment(forecast);
            }
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(DetailedForecastActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    startActivity(new Intent(DetailedForecastActivity.this, SearchActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    startActivity(new Intent(DetailedForecastActivity.this, MapActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_compare) {
                    startActivity(new Intent(DetailedForecastActivity.this, WeatherComparisonActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }

    private void showDetailedForecastFragment(Forecast forecast) {
        DetailedForecastFragment fragment = DetailedForecastFragment.newInstance(forecast);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}