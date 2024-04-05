package org.me.gcu.adekunle_ganiyat_s2110996.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrentWeather implements Parcelable {
    private String title;
    private float temperature;
    private String windDirection;
    private String windSpeed;
    private String humidity;
    private String pressure;
    private String visibility;

    public CurrentWeather() {
        // Default constructor
    }

    public CurrentWeather(String title, float temperature, String windDirection, String windSpeed, String humidity, String pressure, String visibility) {
        this.title = title;
        this.temperature = temperature;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
        this.pressure = pressure;
        this.visibility = visibility;
    }

    protected CurrentWeather(Parcel in) {
        title = in.readString();
        temperature = in.readFloat();
        windDirection = in.readString();
        windSpeed = in.readString();
        humidity = in.readString();
        pressure = in.readString();
        visibility = in.readString();
    }

    public static final Creator<CurrentWeather> CREATOR = new Creator<CurrentWeather>() {
        @Override
        public CurrentWeather createFromParcel(Parcel in) {
            return new CurrentWeather(in);
        }

        @Override
        public CurrentWeather[] newArray(int size) {
            return new CurrentWeather[size];
        }
    };

    public CurrentWeather(float temperature, String title, String humidity) {
        this.temperature = temperature;
        this.title = title;
        this.humidity = humidity;
    }

    // Getters and setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeFloat(temperature);
        dest.writeString(windDirection);
        dest.writeString(windSpeed);
        dest.writeString(humidity);
        dest.writeString(pressure);
        dest.writeString(visibility);
    }
}