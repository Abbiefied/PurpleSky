package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments.HeaderFragment;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SettingsViewModel;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    private static final String TITLE_TAG = "settingsActivityTitle";
    private static final int REQUEST_CODE_WIDGET_PICK = 1;

    private String locationId;

    private BottomNavigationView bottomNavigationView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Retrieve the locationId from the intent extras
        Intent intent = getIntent();
        locationId = intent.getStringExtra("locationId");

        // Initialize the SettingsViewModel
        SettingsViewModel settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        // Set preference change listeners
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(mPreferenceChangeListener);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(-1);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.navigation_home) {
                    startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_search) {
                    startActivity(new Intent(SettingsActivity.this, SearchActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_map) {
                    startActivity(new Intent(SettingsActivity.this, MapActivity.class));
                    return true;
                } else if (itemId == R.id.navigation_compare) {
                    startActivity(new Intent(SettingsActivity.this, WeatherComparisonActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getLocationId() {
        return locationId;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener mPreferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    switch (key) {
                        case "refresh_data":
                            // Refresh weather data using the locationId
                            SettingsViewModel settingsViewModel = new ViewModelProvider(SettingsActivity.this).get(SettingsViewModel.class);
                            settingsViewModel.refreshWeatherData(locationId);
                            break;
                        case "data_refresh_frequency":
                            updateDataRefreshFrequency(sharedPreferences);
                            break;
                        case "notifications":
                            updateNotificationSettings(sharedPreferences);
                            break;
                        case "add_widget":
                            updateWidgetPreference(sharedPreferences);
                            break;
                        case "theme":
                            updateAppTheme(sharedPreferences);
                            break;

                    }
                }
            };

    private void updateDataRefreshFrequency(SharedPreferences sharedPreferences) {
        String frequencyValue = sharedPreferences.getString("data_refresh_frequency", "3600");
        long refreshInterval = Long.parseLong(frequencyValue);
    }

    private void updateNotificationSettings(SharedPreferences sharedPreferences) {
        boolean notificationsEnabled = sharedPreferences.getBoolean("notifications", true);
        // Update the notification settings in your app
        // Enable or disable notifications based on the user's preference
    }

    private void updateWidgetPreference(SharedPreferences sharedPreferences) {
        boolean addWidget = sharedPreferences.getBoolean("add_widget", false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WIDGET_PICK && resultCode == RESULT_OK) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                // Widget picked, create an instance of the widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                WeatherWidgetProvider weatherWidgetProvider = new WeatherWidgetProvider();
                weatherWidgetProvider.onUpdate(this, appWidgetManager, new int[]{appWidgetId});
            }
        }
    }

    private void updateAppTheme(SharedPreferences sharedPreferences) {
        String selectedTheme = sharedPreferences.getString("theme", "light");
        // Update the app's theme based on the user's selection
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
            // Instantiate the new Fragment
            final Bundle args = pref.getExtras();
            final Fragment fragment;
            if (pref.getKey().equals("header_preferences")) {
                fragment = new HeaderFragment();
            } else {
                fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                        getClassLoader(),
                        pref.getFragment());
            }
            fragment.setArguments(args);
            fragment.setTargetFragment(caller, 0);
            // Replace the existing Fragment with the new Fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings, fragment)
                    .addToBackStack(null)
                    .commit();
            setTitle(pref.getTitle());
            return true;
        }

    public static class NotificationsFragment extends PreferenceFragmentCompat {
        private SettingsViewModel settingsViewModel;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.notifications_preferences, rootKey);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

            SwitchPreference notificationsPreference = findPreference("notifications");
            notificationsPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean enabled = (Boolean) newValue;
                    settingsViewModel.setNotificationsEnabled(enabled);
                    return true;
                }
            });

            settingsViewModel.getNotificationsEnabled().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean enabled) {
                    notificationsPreference.setChecked(enabled);
                }
            });
        }
    }
}