package org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.RecentLocation;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<String> searchTerm = new MutableLiveData<>();
    private MutableLiveData<String> defaultLocation = new MutableLiveData<>();
    private MutableLiveData<List<RecentLocation>> recentLocations = new MutableLiveData<>(new ArrayList<>());

    private MutableLiveData<String> searchedLocation = new MutableLiveData<>();

    public void setSearchTerm(String term) {
        searchTerm.setValue(term);
    }

    public LiveData<String> getSearchTerm() {
        return searchTerm;
    }

    public void setDefaultLocation(String location) {
        defaultLocation.setValue(location);
    }

    public LiveData<String> getDefaultLocation() {
        return defaultLocation;
    }

    public void setSearchedLocation(String location) {
        searchedLocation.setValue(location);
    }

    public LiveData<String> getSearchedLocation() {
        return searchedLocation;
    }
    public void addRecentLocation(RecentLocation recentLocation) {
        List<RecentLocation> locations = recentLocations.getValue();
        if (locations != null) {
            locations.add(0, recentLocation);
            if (locations.size() > 3) {
                locations.remove(locations.size() - 1);
            }
            recentLocations.setValue(locations);
        }
    }

    public LiveData<List<RecentLocation>> getRecentLocations() {
        return recentLocations;
    }
}
