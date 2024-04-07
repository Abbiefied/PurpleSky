package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
}