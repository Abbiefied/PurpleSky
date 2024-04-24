package org.me.gcu.adekunle_ganiyat_s2110996.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.me.gcu.adekunle_ganiyat_s2110996.R;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;

public class AirQualityActivity extends AppCompatActivity {
    private TextView mAirQualityIndexTextView;
    private TextView mCategoryTextView;
    private TextView mDominantPollutantTextView;
    private TextView mPollutantsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);

        mAirQualityIndexTextView = findViewById(R.id.air_quality_index_text_view);
        mCategoryTextView = findViewById(R.id.category_text_view);
        mDominantPollutantTextView = findViewById(R.id.dominant_pollutant_text_view);
        mPollutantsTextView = findViewById(R.id.pollutants_text_view);

        AirQualityData airQualityData = getIntent().getParcelableExtra("air_quality_data");
        if (airQualityData != null) {
            displayAirQualityData(airQualityData);
        }
    }

    private void displayAirQualityData(AirQualityData airQualityData) {
        mAirQualityIndexTextView.setText("AQI: " + airQualityData.getAirQualityIndex());

        // Find the desired air quality index (e.g., "uaqi" or "gbr_defra")
        AirQualityData.AirQualityIndex selectedIndex = null;
        for (AirQualityData.AirQualityIndex index : airQualityData.getIndexes()) {
            if (index.getCode().equals("uaqi")) {
                selectedIndex = index;
                break;
            }
        }

        if (selectedIndex != null) {
            mCategoryTextView.setText("Category: " + selectedIndex.getCategory());
            mDominantPollutantTextView.setText("Dominant Pollutant: " + selectedIndex.getDominantPollutant());
        }

        StringBuilder pollutantsBuilder = new StringBuilder();
        for (AirQualityData.Pollutant pollutant : airQualityData.getPollutants()) {
            pollutantsBuilder.append(pollutant.getDisplayName())
                    .append(": ")
                    .append(pollutant.getConcentration().getValue())
                    .append(" ")
                    .append(pollutant.getConcentration().getUnits())
                    .append("\n");
        }
        mPollutantsTextView.setText(pollutantsBuilder.toString());
    }
}