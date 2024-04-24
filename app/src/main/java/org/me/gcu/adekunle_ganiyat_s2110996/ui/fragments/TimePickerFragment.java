package org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.SettingsViewModel;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private OnTimeSelectedListener timeSelectedListener;

    private static final String ARG_HOUR = "hour";
    private static final String ARG_MINUTE = "minute";

    private int hour;
    private int minute;

        public static TimePickerFragment newInstance(int hour, int minute, OnTimeSelectedListener listener) {
            TimePickerFragment fragment = new TimePickerFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_HOUR, hour);
            args.putInt(ARG_MINUTE, minute);
            fragment.setArguments(args);
            fragment.setTimeSelectedListener(listener);
            return fragment;
        }

        public void setTimeSelectedListener(OnTimeSelectedListener listener) {
            timeSelectedListener = listener;
        }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            hour = getArguments().getInt(ARG_HOUR);
            minute = getArguments().getInt(ARG_MINUTE);
        }

        return new TimePickerDialog(requireContext(), this, hour, minute, DateFormat.is24HourFormat(requireContext()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Logic to handle the selected time
        // Save the selected time to SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("refresh_hour", hourOfDay);
        editor.putInt("refresh_minute", minute);
        editor.apply();

        if (timeSelectedListener != null) {
            timeSelectedListener.onTimeSelected(hourOfDay, minute);
        }

        // Show a success message
        Toast.makeText(requireContext(), "Data refresh time set successfully", Toast.LENGTH_SHORT).show();

    }

    public interface OnTimeSelectedListener {
        void onTimeSelected(int hour, int minute);
    }

//    private void refreshWeatherData() {
//        SettingsViewModel settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
//        settingsViewModel.refreshWeatherData(requireContext(), Location.getDefaultLocationId());
//    }
}