package org.me.gcu.adekunle_ganiyat_s2110996.data.models;

public class RecentLocation {
    private String locationName;
    private String weatherSummary;

    public RecentLocation(String locationName, String weatherSummary) {
        this.locationName = locationName;
        this.weatherSummary = weatherSummary;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getWeatherSummary() {
        return weatherSummary;
    }
}