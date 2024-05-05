package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.PollutantAdapter;

public class AirQualityActivity extends AppCompatActivity {
    private TextView mAirQualityIndexTextView;
    private TextView mCategoryTextView;
    private TextView mDominantPollutantTextView;
    private boolean isExpanded = false;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        mAirQualityIndexTextView = findViewById(R.id.air_quality_index_text_view);
        mCategoryTextView = findViewById(R.id.category_text_view);
        mDominantPollutantTextView = findViewById(R.id.dominant_pollutant_text_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LinearLayout pollutantsHeader = findViewById(R.id.pollutants_header);
        ImageView expandCollapseIcon = findViewById(R.id.expand_collapse_icon);
        RecyclerView pollutantsRecyclerView = findViewById(R.id.pollutants_recycler_view);

        pollutantsHeader.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            pollutantsRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            expandCollapseIcon.animate()
                    .rotation(isExpanded ? 180f : 0f)
                    .setDuration(300)
                    .start();
        });

        AirQualityData airQualityData = getIntent().getParcelableExtra("air_quality_data");
        if (airQualityData != null) {
            displayAirQualityData(airQualityData);
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(AirQualityActivity.this, MainActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    startActivity(new Intent(AirQualityActivity.this, SearchActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    startActivity(new Intent(AirQualityActivity.this, MapActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_compare) {
                    startActivity(new Intent(AirQualityActivity.this, WeatherComparisonActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void displayAirQualityData(AirQualityData airQualityData) {
        mAirQualityIndexTextView.setText("AQI: " + airQualityData.getAirQualityIndex());

        // Find the desired air quality index (e.g., "uaqi" or "gbr_defra")
        AirQualityData.AirQualityIndex selectedIndex = null;
        for (AirQualityData.AirQualityIndex index : airQualityData.getIndexes()) {
            if (index.getCode().equals("uaqi")) {
                selectedIndex = index;
                break;
            }
        }

        if (selectedIndex != null) {
            mCategoryTextView.setText("Category: " + selectedIndex.getCategory());
            mDominantPollutantTextView.setText("Dominant Pollutant: " + selectedIndex.getDominantPollutant());
        }

        RecyclerView pollutantsRecyclerView = findViewById(R.id.pollutants_recycler_view);
        PollutantAdapter adapter = new PollutantAdapter(airQualityData.getPollutants());
        pollutantsRecyclerView.setAdapter(adapter);
        pollutantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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