package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.fragments.DetailedForecastFragment;
import org.me.gcu.adekunle_ganiyat_s2110996.ui.viewmodels.WeatherViewModel;

public class DetailedForecastActivity extends AppCompatActivity {

    private WeatherViewModel weatherViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_forecast);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        if (getIntent() != null && getIntent().hasExtra("forecast")) {
            Forecast forecast = getIntent().getParcelableExtra("forecast");
            if (forecast != null) {
                showDetailedForecastFragment(forecast);
            }
        }
    }

    private void showDetailedForecastFragment(Forecast forecast) {
        DetailedForecastFragment fragment = DetailedForecastFragment.newInstance(forecast);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}