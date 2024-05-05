//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.RecentLocationAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.SearchAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SearchAdapter.OnLocationClickListener, RecentLocationAdapter.OnRecentLocationClickListener {
    private SearchViewModel searchViewModel;
    private EditText searchEditText;
    private ImageView searchIcon;
    private RecyclerView suggestionsRecyclerView;
    private RecyclerView recentLocationsRecyclerView;
    private SearchAdapter searchAdapter;
    private RecentLocationAdapter recentLocationAdapter;

    private BottomNavigationView bottomNavigationView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        searchEditText = findViewById(R.id.search_edit_text);
        searchIcon = findViewById(R.id.search_icon);
        suggestionsRecyclerView = findViewById(R.id.suggestions_recycler_view);
        recentLocationsRecyclerView = findViewById(R.id.recent_locations_recycler_view);

        searchAdapter = new SearchAdapter(new ArrayList<>(), this);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionsRecyclerView.setAdapter(searchAdapter);

        WeatherRepository weatherRepository = new WeatherRepository(getApplicationContext());
        recentLocationAdapter = new RecentLocationAdapter(new ArrayList<>(), this, weatherRepository);
        recentLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentLocationsRecyclerView.setAdapter(recentLocationAdapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    suggestionsRecyclerView.setVisibility(View.GONE);
                } else {
                    suggestionsRecyclerView.setVisibility(View.VISIBLE);
                    searchViewModel.fetchSearchSuggestions(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchIcon.setOnClickListener(v -> {
            String searchQuery = searchEditText.getText().toString().trim();
            if (!searchQuery.isEmpty()) {
                Location selectedLocation = new Location(searchQuery);
                searchViewModel.addRecentLocation(selectedLocation);
                String locationName = selectedLocation.getName();

                navigateToMainActivity(selectedLocation);
            }
        });

        searchViewModel.getSearchSuggestionsLiveData().observe(this, locations -> {
            searchAdapter.updateData(locations);
        });
        setupObservers();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        int selectedMenuItem = getIntent().getIntExtra("selectedMenuItem", R.id.navigation_search);
        bottomNavigationView.setSelectedItemId(selectedMenuItem);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    Intent homeIntent = new Intent(SearchActivity.this, MainActivity.class);
                    homeIntent.putExtra("selectedMenuItem", R.id.navigation_home);
                    startActivity(homeIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    //No need to start a new activity
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    Intent mapIntent = new Intent(SearchActivity.this, MapActivity.class);
                    mapIntent.putExtra("selectedMenuItem", R.id.navigation_map);
                    startActivity(mapIntent);
                    finish();
                    return true;
                } else if (itemId == R.id.navigation_compare) {
                    Intent compareIntent = new Intent(SearchActivity.this, WeatherComparisonActivity.class);
                    compareIntent.putExtra("selectedMenuItem", R.id.navigation_compare);
                    startActivity(compareIntent);
                    finish();
                    return true;
                }
                return false;
            }
        });

    }

    private void setupObservers() {
        searchViewModel.getRecentLocationsLiveData().observe(this, recentLocations -> {
            recentLocationAdapter.updateData(recentLocations);
        });
    }

    private void navigateToMainActivity(Location selectedLocation) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("selected_location", selectedLocation);
        startActivity(intent);
    }

    @Override
    public void onLocationClick(Location location) {
        searchViewModel.addRecentLocation(location);
        navigateToMainActivity(location);
    }

    @Override
    public void onRecentLocationClick(Location location) {
        searchViewModel.addRecentLocation(location);
        navigateToMainActivity(location);
    }


    @Override
    protected void onResume() {
        super.onResume();
        searchViewModel.fetchRecentLocations();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed(); // Call the superclass implementation
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}