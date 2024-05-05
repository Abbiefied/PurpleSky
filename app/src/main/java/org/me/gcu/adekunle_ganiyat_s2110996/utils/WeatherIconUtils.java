package org.me.gcu.adekunle_ganiyat_s2110996.utils;

import org.me.gcu.adekunle_ganiyat_s2110996.R;

public class WeatherIconUtils {

    public static int getWeatherIconResId(String weatherCondition, float temperature) {
        switch (weatherCondition) {
            case "Light Rain":
                return R.drawable.mooncloud_mid_rain;
            case "Cloudy":
                return R.drawable.cloudy;
            case "Partly Cloudy":
                return R.drawable.cloudy_sunny;
            case "Sunny":
                return R.drawable.sun;
            case "Rain Showers":
                return R.drawable.sun_cloud_angled_rain;
            case "Sunny Intervals":
                return R.drawable.sunny;
            case "Heavy Rain":
                return R.drawable.rainy;
            case "Thick Cloud":
                return R.drawable.cloudy;
            case "Thundery Showers":
                return R.drawable.thunder;
            case "Drizzle":
                return R.drawable.suncloud_mid_rain;
            case "Mist":
                return R.drawable.snowy;
            case "Light Cloud":
                return R.drawable.mooncloud_fast_wind;
            case "Clear Sky":
                return R.drawable.weather;
            default:
                // If weather condition doesn't match any of the listed conditions or is null,
                // fall back to temperature-based icon selection
                return getWeatherIconResIdBasedOnTemperature(temperature);
        }
    }

    public static int getWeatherIconResIdBasedOnTemperature(float temperature) {
        if (temperature < 0) {
            return R.drawable.mooncloud_mid_rain;
        } else if (temperature < 10) {
            return R.drawable.mooncloud_fast_wind;
        } else if (temperature < 20) {
            return R.drawable.suncloud_mid_rain_small;
        } else if (temperature < 30) {
            return R.drawable.cloudy_sunny;
        } else {
            return R.drawable.sunny;
        }
    }
}