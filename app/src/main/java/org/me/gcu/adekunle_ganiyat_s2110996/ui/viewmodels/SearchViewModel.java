//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.SearchRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private final SearchRepository searchRepository;
    private final MutableLiveData<String> searchQueryLiveData;
    private final MutableLiveData<List<Location>> searchSuggestionsLiveData;
    private final MutableLiveData<List<Location>> recentLocationsLiveData;

    public SearchViewModel(Application application) {
        super(application);
        searchRepository = new SearchRepository(application);
        searchQueryLiveData = new MutableLiveData<>();
        searchSuggestionsLiveData = new MutableLiveData<>();
        recentLocationsLiveData = new MutableLiveData<>();
    }

    public LiveData<String> getSearchQueryLiveData() {
        return searchQueryLiveData;
    }

    public LiveData<List<Location>> getSearchSuggestionsLiveData() {
        return searchSuggestionsLiveData;
    }
    public LiveData<List<Location>> getRecentLocationsLiveData() {
        return recentLocationsLiveData;
    }

    public void fetchSearchSuggestions(String query) {
        searchRepository.fetchSearchSuggestions(query, new SearchRepository.SearchCallback<List<Location>>() {
            @Override
            public void onSuccess(List<Location> locations) {
                searchSuggestionsLiveData.setValue(locations);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("SearchViewModel", "Failed to fetch search suggestions: " + errorMessage);
            }
        });
    }

    public void addRecentLocation(Location location) {
        searchRepository.addRecentLocation(location, new SearchRepository.SearchCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                fetchRecentLocations();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("SearchViewModel", "Failed to add recent location: " + errorMessage);
            }
        });
    }

    public void fetchRecentLocations() {
        searchRepository.fetchRecentLocations(new SearchRepository.SearchCallback<List<Location>>() {
            @Override
            public void onSuccess(List<Location> locations) {
                recentLocationsLiveData.setValue(locations);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("SearchViewModel", "Failed to fetch recent locations: " + errorMessage);
            }
        });
    }

    public void setSearchQuery(String query) {
        searchQueryLiveData.setValue(query);
        fetchSearchSuggestions(query);
    }

    public void clearSearchSuggestions() {
        searchSuggestionsLiveData.setValue(new ArrayList<>());
    }
}