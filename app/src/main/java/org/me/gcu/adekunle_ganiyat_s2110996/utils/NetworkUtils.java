package org.me.gcu.adekunle_ganiyat_s2110996.utils;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.net.Uri;
import android.util.Log;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetworkUtils {

    private static final String BASE_URL = "https://weather-broker-cdn.api.bbci.co.uk/en/";
    private static final String CURRENT_WEATHER_ENDPOINT = "observation/rss/";
    private static final String FORECAST_ENDPOINT = "forecast/rss/3day/";

    public static String buildCurrentWeatherUrl(String locationId) {
        return Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(CURRENT_WEATHER_ENDPOINT)
                .appendPath(locationId)
                .build()
                .toString();
    }

    public static String buildForecastUrl(String locationId) {
        return Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(FORECAST_ENDPOINT)
                .appendPath(locationId)
                .build()
                .toString();
    }

    public static String getResponseFromHttpUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            int character;
            while ((character = inputStream.read()) != -1) {
                builder.append((char) character);
            }
            return builder.toString();
        } finally {
            urlConnection.disconnect();
        }
    }

    public static CurrentWeather parseCurrentWeatherXml(String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new java.io.StringReader(xml));

        CurrentWeather currentWeather = null;

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                currentWeather = new CurrentWeather();
            } else if (eventType == XmlPullParser.START_TAG && "title".equals(parser.getName())) {
                if (currentWeather != null) {
                    String title = parser.nextText();
                    currentWeather.setTitle(title);
                    // Extract temperature from the title
                    String[] parts = title.split(",");
                    if (parts.length > 1) {
                        String temperatureStr = parts[1].trim().split("°")[0].trim();
                        temperatureStr = temperatureStr.replace("Â", ""); // Remove the special character
                        float temperature = Float.parseFloat(temperatureStr);
                        currentWeather.setTemperature(temperature);
                    }
                }
            } else if (eventType == XmlPullParser.START_TAG && "description".equals(parser.getName())) {
                if (currentWeather != null) {
                    String description = parser.nextText();
                    // Extract other weather details from the description
                    String[] parts = description.split(",");
                    for (String part : parts) {
                        String[] keyValue = part.trim().split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();
                            switch (key) {
                                case "Wind Direction":
                                    currentWeather.setWindDirection(value);
                                    break;
                                case "Wind Speed":
                                    currentWeather.setWindSpeed(value);
                                    break;
                                case "Humidity":
                                    currentWeather.setHumidity(value);
                                    break;
                                case "Pressure":
                                    currentWeather.setPressure(value);
                                    break;
                                case "Visibility":
                                    currentWeather.setVisibility(value);
                                    break;
                            }
                        }
                    }
                }
            }
            eventType = parser.next();
        }

        return currentWeather;
    }

    public static List<Forecast> parseForecastXml(String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new java.io.StringReader(xml));

        List<Forecast> forecastList = new ArrayList<>();

        int eventType = parser.getEventType();
        Forecast forecast = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                forecast = new Forecast();
            } else if (eventType == XmlPullParser.START_TAG && "title".equals(parser.getName())) {
                if (forecast != null) {
                    String title = parser.nextText();
                    forecast.setTitle(title);
                        // Extract minTemp and maxTemp in Celsius and Fahrenheit
                    if (title != null) {
                        String[] tempParts = title.split("Minimum Temperature: ");
                        if (tempParts.length >= 2) {
                            String minTempCelsius = tempParts[1].split("Â")[0].trim();
                            forecast.setMinTemperatureCelcius(Float.parseFloat(minTempCelsius));

                            // Extract maxTemp Celsius if available
                            String[] maxTempParts = title.split("Maximum Temperature: ");
                            if (maxTempParts.length >= 2) {
                                String maxTempCelsius = maxTempParts[1].split("Â")[0].trim();
                                forecast.setMaxTemperatureCelcius(Float.parseFloat(maxTempCelsius));
                            } else {
                                forecast.setMaxTemperatureCelcius(Float.NaN);
                            }

                            // Extract maxTemp Fahrenheit if available
                            String[] fahrenheitParts = title.split("\\( |\\)");
                            if (fahrenheitParts.length >= 3) {
                                String maxTempFahrenheit = fahrenheitParts[2].split("°")[0].trim();
                                forecast.setMaxTemperatureFahrenheit(Float.parseFloat(maxTempFahrenheit));
                            } else {
                                forecast.setMaxTemperatureFahrenheit(Float.NaN);
                            }
                        }
                    }
                }
            } else if (eventType == XmlPullParser.START_TAG && "description".equals(parser.getName())) {
                if (forecast != null) {
                    String description = parser.nextText();
                    // Extract other forecast details from the description
                    String[] parts = description.split(",");
                    for (String part : parts) {
                        String[] keyValue = part.trim().split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();
                            switch (key) {
                                case "Wind Direction":
                                    forecast.setWindDirection(value);
                                    break;
                                case "Wind Speed":
                                    forecast.setWindSpeed(Float.parseFloat(value.split("mph")[0].trim()));
                                    break;
                                case "Visibility":
                                    forecast.setVisibility(value);
                                    break;
                                case "Pressure":
                                    forecast.setPressure(value.split("mb")[0].trim());
                                    break;
                                case "Humidity":
                                    forecast.setHumidity(value.split("%")[0].trim());
                                    break;
                                case "UV Risk":
                                    forecast.setUvRisk(value);
                                    break;
                                case "Pollution":
                                    forecast.setPollution(value);
                                    break;
                                case "Sunrise":
                                    forecast.setSunrise(value);
                                    break;
                                case "Sunset":
                                    forecast.setSunset(value);
                                    break;
                            }
                        }
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG && "item".equals(parser.getName())) {
                if (forecast != null) {
                    Log.d(TAG, "Received forecast Max Temp weather XML data: " + forecast.getMaxTemperatureCelcius());
                    Log.d(TAG, "Received forecast Min Temp weather XML data: " + forecast.getMinTemperatureCelcius());
                    forecastList.add(forecast);
                    forecast = null;
                }
            }
            eventType = parser.next();
        }

        return forecastList;

    }
}