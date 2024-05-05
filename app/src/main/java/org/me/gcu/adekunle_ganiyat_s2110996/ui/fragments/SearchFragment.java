//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.activities.MainActivity;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.RecentLocationAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.SearchAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchAdapter.OnLocationClickListener, RecentLocationAdapter.OnRecentLocationClickListener {
    private SearchViewModel searchViewModel;
    private EditText searchEditText;
    private ImageView searchIcon;
    private RecyclerView suggestionsRecyclerView;
    private RecyclerView recentLocationsRecyclerView;
    private SearchAdapter searchAdapter;
    private RecentLocationAdapter recentLocationAdapter;
    private WeatherRepository weatherRepository;

    private List<Location> recentLocations = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = view.findViewById(R.id.search_edit_text);
        searchIcon = view.findViewById(R.id.search_icon);
        suggestionsRecyclerView = view.findViewById(R.id.suggestions_recycler_view);
        recentLocationsRecyclerView = view.findViewById(R.id.recent_locations_recycler_view);

        ImageView backIcon = view.findViewById(R.id.back_icon);
        backIcon.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        setupSearchEditText();
        setupSearchIcon();
        setupSuggestionsRecyclerView();
        setupRecentLocationsRecyclerView();
        initWeatherRepository();
    }

    private void initWeatherRepository() {
        weatherRepository = new WeatherRepository(requireContext());
    }

    private void setupSearchEditText() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchViewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void setupSearchIcon() {
        searchIcon.setOnClickListener(v -> performSearch());
    }

    private void setupSuggestionsRecyclerView() {
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        searchAdapter = new SearchAdapter(new ArrayList<>(), this);
        suggestionsRecyclerView.setAdapter(searchAdapter);

        searchViewModel.getSearchSuggestionsLiveData().observe(getViewLifecycleOwner(), locations -> {
            searchAdapter.updateData(locations);
        });
    }

    private void setupRecentLocationsRecyclerView() {
        recentLocationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recentLocationAdapter = new RecentLocationAdapter(recentLocations, this, weatherRepository);
        recentLocationsRecyclerView.setAdapter(recentLocationAdapter);

        searchViewModel.getRecentLocationsLiveData().observe(getViewLifecycleOwner(), locations -> {
            recentLocationAdapter.updateData(locations);
        });
    }

    private void performSearch() {
        String query = searchEditText.getText().toString().trim();
        if (!query.isEmpty()) {
            Location location = new Location(query);
            searchViewModel.addRecentLocation(location);
            navigateToMainActivity(location);
        }
    }

    private void navigateToMainActivity(Location location) {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.putExtra("selected_location", location);
        startActivity(intent);
    }

    private void addToRecentLocations(Location location) {
        searchViewModel.addRecentLocation(location);
    }

    private void onSearchQueryChanged(String query) {
        if (query.isEmpty()) {
            // Clear the suggestions when the query is empty
            searchViewModel.clearSearchSuggestions();
        } else {
            searchViewModel.fetchSearchSuggestions(query);
        }
    }

    @Override
    public void onLocationClick(Location location) {
        addToRecentLocations(location);
        navigateToMainActivity(location);
    }

    @Override
    public void onRecentLocationClick(Location location) {
        addToRecentLocations(location);
        navigateToMainActivity(location);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchViewModel.fetchRecentLocations();
    }
}