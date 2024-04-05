package org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.activities.MainActivity;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.RecentLocation;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.RecentLocationAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements RecentLocationAdapter.OnRecentLocationClickListener {

    private SearchViewModel searchViewModel;
    private AutoCompleteTextView searchBox;
    private ImageView searchButton;
    private RecyclerView recentLocationsRecyclerView;
    private RecentLocationAdapter recentLocationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        searchBox = view.findViewById(R.id.search_box);
        searchButton = view.findViewById(R.id.search_button);
        recentLocationsRecyclerView = view.findViewById(R.id.recent_locations_recycler_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        setupSearchBox();
        setupRecentLocations();
    }

    private void setupSearchBox() {
        // Set up the AutoCompleteTextView with location suggestions
        String[] locations = getResources().getStringArray(R.array.locations);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locations);
        searchBox.setAdapter(adapter);

        // Set up the search button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchBox.getText().toString().trim();
                if (!searchTerm.isEmpty()) {
                    searchLocation(searchTerm);

                }
            }
        });

        // Set up the search action listener for the keyboard
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchTerm = searchBox.getText().toString().trim();
                    if (!searchTerm.isEmpty()) {
                        searchLocation(searchTerm);
                        return true;
                    }
                }
                return false;
            }
        });

        // Set up the item click listener for the AutoCompleteTextView
        searchBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String searchTerm = (String) parent.getItemAtPosition(position);
                searchLocation(searchTerm);
            }
        });
    }

    private void searchLocation(String locationName) {
        // Add the searched location to the recent locations list
        String weatherSummary = "Weather summary for " + locationName; // Replace with actual weather summary
        RecentLocation recentLocation = new RecentLocation(locationName, weatherSummary);
        searchViewModel.addRecentLocation(recentLocation);

        // Set the searched location in the ViewModel
        searchViewModel.setSearchedLocation(locationName);

        // Navigate to MainActivity
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
    }
    private void setupRecentLocations() {
        recentLocationAdapter = new RecentLocationAdapter(new ArrayList<>(), this);
        recentLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recentLocationsRecyclerView.setAdapter(recentLocationAdapter);

        searchViewModel.getRecentLocations().observe(getViewLifecycleOwner(), new Observer<List<RecentLocation>>() {
            @Override
            public void onChanged(List<RecentLocation> recentLocations) {
                recentLocationAdapter.setRecentLocations(recentLocations);
            }
        });
    }

    @Override
    public void onRecentLocationClick(RecentLocation recentLocation) {
        // Navigate to MainActivity and pass the selected recent location
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("locationName", recentLocation.getLocationName());
        startActivity(intent);
    }
}