package org.me.gcu.adekunle_ganiyat_s2110996.utils;

import java.util.Calendar;

public class DateUtils {

    public static String getGreeting() {
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= 5 && hourOfDay < 12) {
            return "Good morning";
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            return "Good afternoon";
        } else if (hourOfDay >= 18 && hourOfDay < 22) {
            return "Good evening";
        } else {
            return "Good night";
        }
    }
}
