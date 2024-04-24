package org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.activities.SettingsActivity;
import org.me.gcu.adekunle_ganiyat_s2110996.data.sources.LocalDataSource;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SettingsViewModel;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.AppExecutors;

import java.util.Locale;

public class HeaderFragment extends PreferenceFragmentCompat implements TimePickerFragment.OnTimeSelectedListener {
    private SettingsViewModel settingsViewModel;
    private static final int REQUEST_CODE_WIDGET_PICK = 1;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.header_preferences, rootKey);

        // Set click listener for "Data Refresh Frequency" preference
        Preference dataRefreshFrequencyPreference = findPreference("data_refresh_frequency");
        dataRefreshFrequencyPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showTimePickerDialog();
                return true;
            }
        });

        // Set the summary of the "Data Refresh Frequency" preference to the previously set time
        updateDataRefreshFrequencySummary(dataRefreshFrequencyPreference);

        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Set click listener for "Refresh Data" preference
        Preference refreshDataPreference = findPreference("refresh_data");
        refreshDataPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // Refresh weather data using the locationId from the activity
                SettingsActivity settingsActivity = (SettingsActivity) getActivity();
                String locationId = settingsActivity.getLocationId();
                settingsViewModel.refreshWeatherData(locationId);
                return true;
            }
        });

        // Set click listener for "Add Widget to Homescreen" preference
        Preference addWidgetPreference = findPreference("add_widget");
        addWidgetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
                startActivityForResult(intent, REQUEST_CODE_WIDGET_PICK);
                return true;
            }
        });

        // Set click listener for "Delete Cached Data" preference
        Preference deleteCachePreference = findPreference("delete_cache");
        deleteCachePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                showDeleteCacheConfirmationDialog();
                return true;
            }
        });


    }

    private void deleteCachedData() {
        Context context = requireContext();
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                LocalDataSource localDataSource = new LocalDataSource(context);
                localDataSource.deleteCachedData();

                // Show success message on the main thread
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Cached data deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showDeleteCacheConfirmationDialog() {
        Context context = requireContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_delete_cache, null);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);
        Button buttonDelete = dialogView.findViewById(R.id.button_delete);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCachedData();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateDataRefreshFrequencySummary(Preference preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int refreshHour = sharedPreferences.getInt("refresh_hour", 8);
        int refreshMinute = sharedPreferences.getInt("refresh_minute", 0);

        String refreshTime = String.format(Locale.getDefault(), "%02d:%02d", refreshHour, refreshMinute);
        preference.setSummary("Current refresh time: " + refreshTime);
    }

    private void showTimePickerDialog() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int hour = sharedPreferences.getInt("refresh_hour", 8);
        int minute = sharedPreferences.getInt("refresh_minute", 0);

        TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(hour, minute, this);
        timePickerFragment.show(getParentFragmentManager(), "TimePickerFragment");
    }

    @Override
    public void onTimeSelected(int hour, int minute) {
        // Update the summary of the "Data Refresh Frequency" preference
        Preference dataRefreshFrequencyPreference = findPreference("data_refresh_frequency");
        updateDataRefreshFrequencySummary(dataRefreshFrequencyPreference);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observe refresh success LiveData
        settingsViewModel.getRefreshSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // Show a success toast message
                Toast.makeText(requireContext(), "Weather data refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe refresh error LiveData
        settingsViewModel.getRefreshError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                // Display an error message
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

