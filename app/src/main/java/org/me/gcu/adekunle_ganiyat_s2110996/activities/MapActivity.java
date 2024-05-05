package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.data.repositories.WeatherRepository;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.NetworkDataSource;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.adapters.WeatherInfoWindowAdapter;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.AppExecutors;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.CustomMarker;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.WeatherIconUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private SearchView searchView;
    private SimpleCursorAdapter suggestionAdapter;
    private Map<String, CurrentWeather> mWeatherData = new HashMap<>();
    // Set to store existing air quality markers
    Set<Marker> airQualityMarkers = new HashSet<>();

    // Initialize a flag to track if the camera movement was caused by an info window being shown
    boolean isCameraMovedByInfoWindow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        searchView = findViewById(R.id.search_view);
        setupSearchSuggestions(airQualityMarkers);;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query, airQualityMarkers);
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

    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        // Set the map view to Glasgow on load
        LatLng glasgow = new LatLng(55.8642, -4.2518);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(glasgow, 10));

        // Initialize the WeatherInfoWindowAdapter
        WeatherInfoWindowAdapter weatherInfoWindowAdapter = new WeatherInfoWindowAdapter(this);
        googleMap.setInfoWindowAdapter(weatherInfoWindowAdapter);

        // Add markers for specific locations
        for (Location location : Location.getPopularLocations()) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Fetch current weather data for the location
            WeatherRepository weatherRepository = new WeatherRepository(this);
            weatherRepository.fetchCurrentWeather(String.valueOf(location.getId()), new WeatherRepository.WeatherCallback<CurrentWeather>() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    // Create a custom marker with weather data
                    CustomMarker weatherMarker = new CustomMarker(latLng, currentWeather, true);
                    addCustomMarkerToMap(weatherMarker);

                    // Store the current weather data for the location
                    mWeatherData.put(String.valueOf(location.getId()), currentWeather);
                }


                @Override
                public void onFailure(String message) {
                    // Handle failure scenario
                    Log.e("MapActivity", "Failed to fetch current weather data: " + message);
                }
            });

            googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    if (!isCameraMovedByInfoWindow) {
                        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
                        fetchAirQualityForBounds(bounds, airQualityMarkers);
                    }
                    isCameraMovedByInfoWindow = false; // Reset the flag
                }
            });

            // Set an info window click listener
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    isCameraMovedByInfoWindow = true; // Set the flag to true when an info window is clicked
                }
            });
            // Set an info window click listener
            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    isCameraMovedByInfoWindow = true; // Set the flag to true when an info window is clicked
                }
            });

        }

        // Set marker click listener
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Object tag = marker.getTag();
                if (tag instanceof CurrentWeather) {
                    // This is a weather marker, show weather info window
                    marker.showInfoWindow();
                    return true;
                } else if (tag instanceof AirQualityData) {
                    // If it is an air quality marker, navigate to AirQuality activity
                    AirQualityData airQualityData = (AirQualityData) tag;

                    // Navigate to the AirQuality activity
                    Intent intent = new Intent(MapActivity.this, AirQualityActivity.class);
                    intent.putExtra("air_quality_data", airQualityData);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

    }

    //Method to add custom marker to map
    private void addCustomMarkerToMap(CustomMarker customMarker) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(customMarker.getPosition())
                .title(customMarker.isWeatherMarker() ? "Weather" : "Air Quality");
        if (customMarker.isWeatherMarker()) {
            String todayWeatherCondition = customMarker.getCurrentWeather().getWeatherCondition();
            int iconResId = WeatherIconUtils.getWeatherIconResIdBasedOnTemperature(customMarker.getCurrentWeather().getTemperature());
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), iconResId);
            int scaleFactor = 2;
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, originalBitmap.getWidth() * scaleFactor, originalBitmap.getHeight() * scaleFactor, true);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap));
        } else {
            // Set air quality icon
            BitmapDescriptor airQualityIcon = BitmapDescriptorFactory.fromBitmap(createAirQualityMarkerIcon(customMarker.getAirQualityData()));
            markerOptions.icon(airQualityIcon);
        }

        Marker marker = googleMap.addMarker(markerOptions);
        marker.setTag(customMarker); // Store the custom marker object as the tag
    }

    //Method to fetch air quality data for bounds in view
    private void fetchAirQualityForBounds(LatLngBounds bounds, Set<Marker> airQualityMarkers) {
        int numPoints = 8;

        AppExecutors.getInstance().networkIO().execute(() -> {
            for (int i = 0; i < numPoints; i++) {
                double lat = bounds.southwest.latitude + (Math.random() * (bounds.northeast.latitude - bounds.southwest.latitude));
                double lng = bounds.southwest.longitude + (Math.random() * (bounds.northeast.longitude - bounds.southwest.longitude));
                LatLng latLng = new LatLng(lat, lng);

                NetworkDataSource networkDataSource = new NetworkDataSource();
                networkDataSource.fetchAirQualityData(lat, lng, new NetworkDataSource.WeatherCallback<AirQualityData>() {
                    @Override
                    public void onSuccess(AirQualityData airQualityData) {
                        Bitmap markerIcon = createAirQualityMarkerIcon(airQualityData);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
                                .title("Air Quality");

                        AppExecutors.getInstance().mainThread().execute(() -> {
                            if (airQualityMarkers.size() < numPoints) {
                                Marker marker = googleMap.addMarker(markerOptions);
                                marker.setTag(airQualityData);
                                airQualityMarkers.add(marker); // Add the marker to the set
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e(TAG, "Failed to fetch air quality data: " + message);
                    }
                });
            }
        });
    }

    //Method to create air quality marker icon
    private Bitmap createAirQualityMarkerIcon(AirQualityData airQualityData) {
        View markerView = LayoutInflater.from(this).inflate(R.layout.layout_air_quality_marker, null);
        TextView textAirQuality = markerView.findViewById(R.id.text_air_quality);
        TextView textAqiValue = markerView.findViewById(R.id.text_aqi_value);

        String aqiValue = airQualityData.getAirQualityIndex();
        textAqiValue.setText(aqiValue);

        markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        markerView.draw(canvas);

        return bitmap;
    }


    //Method to search for location
    private void searchLocation(String locationName, Set<Marker> airQualityMarkers) {
        // Find the location ID based on the searched location name
        String locationId = Location.getLocationIdByName(locationName);

        if (locationId != null) {
            // Get the latitude and longitude of the location
            Location location = Location.getLocationById(locationId);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Clear existing markers
            googleMap.clear();

            // Fetch and add weather marker for the searched location
            WeatherRepository weatherRepository = new WeatherRepository(this);
            weatherRepository.fetchCurrentWeather(locationId, new WeatherRepository.WeatherCallback<CurrentWeather>() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    // Create a custom marker with weather data
                    CustomMarker weatherMarker = new CustomMarker(latLng, currentWeather, true);
                    addCustomMarkerToMap(weatherMarker);

                    // Store the current weather data for the location
                    mWeatherData.put(locationId, currentWeather);
                }

                @Override
                public void onFailure(String message) {
                    // Handle failure scenario
                    Log.e("MapActivity", "Failed to fetch current weather data: " + message);
                }
            });

            // Zoom the map to the searched location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

            // Fetch and add air quality marker for the searched location
            NetworkDataSource networkDataSource = new NetworkDataSource();
            networkDataSource.fetchAirQualityData(latLng.latitude, latLng.longitude, new NetworkDataSource.WeatherCallback<AirQualityData>() {
                @Override
                public void onSuccess(AirQualityData airQualityData) {
                    Bitmap markerIcon = createAirQualityMarkerIcon(airQualityData);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
                            .title("Air Quality");

                    AppExecutors.getInstance().mainThread().execute(() -> {
                        Marker marker = googleMap.addMarker(markerOptions);
                        marker.setTag(airQualityData);
                        airQualityMarkers.add(marker); // Add the marker to the set
                    });
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "Failed to fetch air quality data: " + message);
                }
            });

        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
        }
    }

    //Method to setup search suggestions
    private void setupSearchSuggestions(Set<Marker> airQualityMarkers) {
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
                searchLocation(query, airQualityMarkers);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
