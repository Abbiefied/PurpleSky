package org.me.gcu.adekunle_ganiyat_s2110996.data.repositories;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Application;
import android.util.Log;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.LocalDataSource;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.NetworkDataSource;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.AppExecutors;

import java.util.List;

public class SearchRepository {
    private final NetworkDataSource networkDataSource;
    private final LocalDataSource localDataSource;
    private final AppExecutors appExecutors;

    public SearchRepository(Application application) {
        networkDataSource = new NetworkDataSource();
        localDataSource = new LocalDataSource(application);
        appExecutors = AppExecutors.getInstance();
    }

    public void fetchSearchSuggestions(String query, SearchCallback<List<Location>> callback) {
        appExecutors.networkIO().execute(() -> {
            networkDataSource.fetchSearchSuggestions(query, new NetworkDataSource.SearchCallback<List<Location>>() {
                @Override
                public void onSuccess(List<Location> data) {
                    appExecutors.mainThread().execute(() -> callback.onSuccess(data));
                }

                @Override
                public void onFailure(String message) {
                    appExecutors.mainThread().execute(() -> callback.onFailure(message));
                }
            });
        });
    }

    public void fetchRecentLocations(SearchCallback<List<Location>> callback) {
        appExecutors.diskIO().execute(() -> {
            List<Location> recentLocations = localDataSource.getRecentLocations();
            appExecutors.mainThread().execute(() -> callback.onSuccess(recentLocations));
        });
    }

    public void addRecentLocation(Location location, SearchCallback<Void> callback) {
        appExecutors.diskIO().execute(() -> {
            localDataSource.addRecentLocation(location);
            appExecutors.mainThread().execute(() -> callback.onSuccess(null));
            Log.d(TAG, "Recent location added: " + location.getName());
        });
    }

    public interface SearchCallback<T> {
        void onSuccess(T data);
        void onFailure(String message);
    }
}