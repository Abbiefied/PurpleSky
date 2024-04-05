package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;


import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.WeatherInfoWindowAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SearchView searchView;
    private SimpleCursorAdapter suggestionAdapter;
    private HeatmapTileProvider mHeatmapTileProvider;
    private TileOverlay mHeatmapOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        searchView = findViewById(R.id.search_view);
        setupSearchSuggestions();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private Map<String, CurrentWeather> mWeatherData = new HashMap<>();

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Set the map view to Glasgow on load
        LatLng glasgow = new LatLng(55.8642, -4.2518);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 10));

        // Add markers for specific locations
        for (Location location : Location.getPopularLocations()) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Fetch current weather data for the location
            WeatherRepository weatherRepository = new WeatherRepository(this);
            weatherRepository.fetchCurrentWeather(String.valueOf(location.getId()), new WeatherRepository.WeatherCallback<CurrentWeather>() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    // Get the weather icon based on temperature
                    int iconResId = WeatherIconUtils.getWeatherIconResId(currentWeather.getTemperature());
                    BitmapDescriptor weatherIcon = BitmapDescriptorFactory.fromResource(iconResId);

                    // Create a custom marker with weather icon
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(location.getName())
                            .icon(weatherIcon);

                    // Add the marker to the map
                    Marker marker = googleMap.addMarker(markerOptions);
                    marker.setTag(String.valueOf(location.getId()));

                    // Store the current weather data for the location
                    mWeatherData.put(String.valueOf(location.getId()), currentWeather);
                }

                @Override
                public void onFailure(String message) {
                    // Handle failure scenario
                    Log.e("MapActivity", "Failed to fetch current weather data: " + message);
                }
            });
        }

        // Set info window adapter
        googleMap.setInfoWindowAdapter(new WeatherInfoWindowAdapter(this, mWeatherData));

        // Set marker click listener
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }
    private void searchLocation(String locationName) {
        // Find the location ID based on the searched location name
        String locationId = Location.getLocationIdByName(locationName);

        if (locationId != null) {
            // Get the latitude and longitude of the location
            Location location = Location.getLocationById(locationId);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Clear existing markers
            googleMap.clear();

            // Add a marker for the searched location
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(locationName);
            Marker marker = googleMap.addMarker(markerOptions);

            // Zoom the map to the searched location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 7));

            // Fetch and display weather information for the searched location
            WeatherRepository weatherRepository = new WeatherRepository(this);
            weatherRepository.fetchCurrentWeather(locationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    // Get the weather icon based on temperature
                    int iconResId = WeatherIconUtils.getWeatherIconResId(currentWeather.getTemperature());
                    BitmapDescriptor weatherIcon = BitmapDescriptorFactory.fromResource(iconResId);

                    // Update the marker icon with the weather icon
                    marker.setIcon(weatherIcon);

                    // Display weather information in the info window
                    marker.setTag(locationId);
                    mWeatherData.put(locationId, currentWeather);
                    marker.showInfoWindow();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(MapActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearchSuggestions() {
        String[] locationNames = getResources().getStringArray(R.array.locations);

        // Create a cursor with location names
        MatrixCursor cursor = new MatrixCursor(new String[]{"_id", "name"});
        for (int i = 0; i < locationNames.length; i++) {
            cursor.addRow(new Object[]{i, locationNames[i]});
        }

        // Create a SimpleCursorAdapter for search suggestions
        String[] from = {"name"};
        int[] to = {android.R.id.text1};
        suggestionAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor, from, to, 0);
        searchView.setSuggestionsAdapter(suggestionAdapter);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) suggestionAdapter.getItem(position);
                int columnIndex = cursor.getColumnIndexOrThrow("name");
                String locationName = cursor.getString(columnIndex);
                searchView.setQuery(locationName, true);
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}