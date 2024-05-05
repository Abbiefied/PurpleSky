//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.utils;

import com.google.android.gms.maps.model.LatLng;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;

// CustomMarker.java
public class CustomMarker {
    private LatLng position;
    private CurrentWeather currentWeather;
    private AirQualityData airQualityData;
    private boolean isWeatherMarker;

    public CustomMarker(LatLng position, CurrentWeather currentWeather, boolean isWeatherMarker) {
        this.position = position;
        this.currentWeather = currentWeather;
        this.isWeatherMarker = isWeatherMarker;
    }

    public CustomMarker(LatLng position, AirQualityData airQualityData, boolean isWeatherMarker) {
        this.position = position;
        this.airQualityData = airQualityData;
        this.isWeatherMarker = isWeatherMarker;
    }

    // Getters and setters

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public AirQualityData getAirQualityData() {
        return airQualityData;
    }

    public void setAirQualityData(AirQualityData airQualityData) {
        this.airQualityData = airQualityData;
    }

    public boolean isWeatherMarker() {
        return isWeatherMarker;
    }

    public void setWeatherMarker(boolean weatherMarker) {
        isWeatherMarker = weatherMarker;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
    }
}

