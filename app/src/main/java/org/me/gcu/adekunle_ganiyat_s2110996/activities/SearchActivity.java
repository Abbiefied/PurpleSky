package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.RecentLocation;
import org.me.gcu.adekunle_ganiyat_s2110996.databinding.ActivitySearchBinding;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.RecentLocationAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments.SearchFragment;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel searchViewModel;
    private RecyclerView recentLocationsRecyclerView;
    private RecentLocationAdapter recentLocationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        recentLocationsRecyclerView = findViewById(R.id.recent_locations_recycler_view);

        // Check if recentLocationsRecyclerView is null before setting layout manager and adapter
        if (recentLocationsRecyclerView != null) {
            recentLocationAdapter = new RecentLocationAdapter(new ArrayList<>(), location -> {
                // Set the searched location in the ViewModel and navigate to MainActivity
                searchViewModel.setSearchedLocation(location.getLocationName());
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            });

            recentLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            recentLocationsRecyclerView.setAdapter(recentLocationAdapter);

            searchViewModel.getRecentLocations().observe(this, new Observer<List<RecentLocation>>() {
                @Override
                public void onChanged(List<RecentLocation> recentLocations) {
                    recentLocationAdapter.setRecentLocations(recentLocations);
                }
            });
        } else {
            Log.e("SearchActivity", "recentLocationsRecyclerView is null");
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new SearchFragment())
                    .commit();
        }
    }
}