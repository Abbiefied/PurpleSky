//
// Name                 Ganiyat Adekunle
// Student ID           S2110996
// Programme of Study   Computing
//

package org.me.gcu.adekunle_ganiyat_s2110996.data.models;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Forecast implements Parcelable {
    private String title;
    private String date;
    private String dayOfWeek;
    private String time;
    private float minTemperatureCelcius;
    private float maxTemperatureCelcius;
    private float minTemperatureFahrenheit;
    private float maxTemperatureFahrenheit;
    private String weatherCondition;
    private String todayWeatherCondition;
    private String windDirection;
    private float windSpeed;
    private String visibility;
    private String pressure;
    private String humidity;
    private String uvRisk;
    private String pollution;
    private String sunrise;
    private String sunset;

    public Forecast() {
        // Default constructor
    }

    public Forecast(String title, String date, String dayOfWeek, String time, float minTemperatureCelcius, float maxTemperatureCelcius, float minTemperatureFahrenheit, float maxTemperatureFahrenheit, String weatherCondition, String todayWeatherCondition, String windDirection, float windSpeed, String visibility, String pressure, String humidity, String uvRisk, String pollution, String sunrise, String sunset) {
        this.title = title;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.minTemperatureCelcius = minTemperatureCelcius;
        this.maxTemperatureCelcius = maxTemperatureCelcius;
        this.minTemperatureFahrenheit = minTemperatureFahrenheit;
        this.maxTemperatureFahrenheit = maxTemperatureFahrenheit;
        this.weatherCondition = weatherCondition;
        this.todayWeatherCondition = todayWeatherCondition;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.visibility = visibility;
        this.pressure = pressure;
        this.humidity = humidity;
        this.uvRisk = uvRisk;
        this.pollution = pollution;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    protected Forecast(Parcel in) {
        title = in.readString();
        date = in.readString();
        dayOfWeek = in.readString();
        time = in.readString();
        minTemperatureCelcius = in.readFloat();
        maxTemperatureCelcius = in.readFloat();
        minTemperatureFahrenheit = in.readFloat();
        maxTemperatureFahrenheit = in.readFloat();
        weatherCondition = in.readString();
        todayWeatherCondition = in.readString();
        windDirection = in.readString();
        windSpeed = in.readFloat();
        visibility = in.readString();
        pressure = in.readString();
        humidity = in.readString();
        uvRisk = in.readString();
        pollution = in.readString();
        sunrise = in.readString();
        sunset = in.readString();
    }

    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };

    public Forecast(String date, String dayOfWeek, float minTemperatureCelcius, float maxTemperatureCelcius, String weatherCondition, String humidity) {
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.minTemperatureCelcius = minTemperatureCelcius;
        this.maxTemperatureCelcius = maxTemperatureCelcius;
        this.weatherCondition = weatherCondition;
        this.humidity = humidity;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public float getMinTemperatureCelcius() {
        return minTemperatureCelcius;
    }

    public void setMinTemperatureCelcius(float minTemperatureCelcius) {
        this.minTemperatureCelcius = minTemperatureCelcius;
    }

    public float getMaxTemperatureCelcius() {
        return maxTemperatureCelcius;
    }

    public void setMaxTemperatureCelcius(float maxTemperatureCelcius) {
        this.maxTemperatureCelcius = maxTemperatureCelcius;
    }

    public float getMinTemperatureFahrenheit() {
        return minTemperatureFahrenheit;
    }

    public void setMinTemperatureFahrenheit(float minTemperatureFahrenheit) {
        this.minTemperatureFahrenheit = minTemperatureFahrenheit;
    }

    public float getMaxTemperatureFahrenheit() {
        return maxTemperatureFahrenheit;
    }

    public void setMaxTemperatureFahrenheit(float maxTemperatureFahrenheit) {
        this.maxTemperatureFahrenheit = maxTemperatureFahrenheit;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }
    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getTodayWeatherCondition() {
        return weatherCondition;
    }
    public void setTodayWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getUvRisk() {
        return uvRisk;
    }

    public void setUvRisk(String uvRisk) {
        this.uvRisk = uvRisk;
    }

    public String getPollution() {
        return pollution;
    }

    public void setPollution(String pollution) {
        this.pollution = pollution;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(dayOfWeek);
        dest.writeString(time);
        dest.writeFloat(minTemperatureCelcius);
        dest.writeFloat(maxTemperatureCelcius);
        dest.writeFloat(minTemperatureFahrenheit);
        dest.writeFloat(maxTemperatureFahrenheit);
        dest.writeString(weatherCondition);
        dest.writeString(todayWeatherCondition);
        dest.writeString(windDirection);
        dest.writeFloat(windSpeed);
        dest.writeString(visibility);
        dest.writeString(pressure);
        dest.writeString(humidity);
        dest.writeString(uvRisk);
        dest.writeString(pollution);
        dest.writeString(sunrise);
        dest.writeString(sunset);
    }
}