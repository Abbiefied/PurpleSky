package org.me.gcu.adekunle_ganiyat_s2110996.utils;

import org.me.gcu.adekunle_ganiyat_s2110996.R;

public class WeatherIconUtils {

    public static int getWeatherIconResId(float temperature) {
        if (temperature < 0) {
            return R.drawable.mooncloud_mid_rain_small;
        } else if (temperature < 10) {
            return R.drawable.mooncloud_mid_rain;
        } else if (temperature < 20) {
            return R.drawable.suncloud_mid_rain_small;
        } else if (temperature < 30) {
            return R.drawable.suncloud_angled_rain_small;
        } else {
            return R.drawable.mooncloud_fast_wind_small;
        }
    }
}
