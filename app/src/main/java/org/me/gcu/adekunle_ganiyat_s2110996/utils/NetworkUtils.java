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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    public static String postRequestToHttpUrl(String urlString, String requestBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setDoOutput(true);

        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(requestBody.getBytes());
        outputStream.flush();
        outputStream.close();

        int responseCode = urlConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            int character;
            while ((character = inputStream.read()) != -1) {
                builder.append((char) character);
            }
            return builder.toString();
        } else {
            throw new IOException("HTTP error code: " + responseCode);
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

                        // Extract temperature in Fahrenheit
                        String temperatureFStr = parts[1].trim().split("\\(")[1].split("°")[0];
                        temperatureFStr = temperatureFStr.replace("Â", ""); // Remove the special character
                        float temperatureF = Float.parseFloat(temperatureFStr);
                        currentWeather.setTemperatureFahrenheit(temperatureF);

                        // Extract day of the week
                        String dayOfWeek = parts[0].split("-")[0].trim();
                        currentWeather.setDayOfWeek(dayOfWeek);
                        Log.d(TAG, "parseCurrentWeatherXml: " + dayOfWeek);
                    }
                }
            } else if (eventType == XmlPullParser.START_TAG && "pubDate".equals(parser.getName())) {
                if (currentWeather != null) {
                    String pubDate = parser.nextText();
                    // Extract date
                    String date = pubDate.split(",")[1].trim();
                    currentWeather.setDate(date);
                    Log.d(TAG, "parseCurrentWeatherXml: date " + date);
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
        int itemCount = 0;
        String baseDate = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && "item".equals(parser.getName())) {
                forecast = new Forecast();
                itemCount++;
            } else if (eventType == XmlPullParser.START_TAG && "title".equals(parser.getName())) {
                if (forecast != null) {
                    String title = parser.nextText();
                    forecast.setTitle(title);
                    // Extract minTemp and maxTemp in Celsius and Fahrenheit
                    if (title != null) {
                        String[] tempParts = title.split("Minimum Temperature: ");
                        if (tempParts.length >= 2) {
                            String minTempCelsius = tempParts[1].split("Â")[0].trim();
                            float minTemperatureCelsius = Float.parseFloat(minTempCelsius);
                            forecast.setMinTemperatureCelcius(Float.isNaN(minTemperatureCelsius) ? 0.0f : minTemperatureCelsius);

                            // Extract maxTemp Celsius if available
                            String[] maxTempParts = title.split("Maximum Temperature: ");
                            if (maxTempParts.length >= 2) {
                                String maxTempCelsius = maxTempParts[1].split("Â")[0].trim();
                                float maxTemperatureCelsius = Float.parseFloat(maxTempCelsius);
                                forecast.setMaxTemperatureCelcius(Float.isNaN(maxTemperatureCelsius) ? 0.0f : maxTemperatureCelsius);
                            } else {
                                forecast.setMaxTemperatureCelcius(0.0f);
                            }

                            // Extract maxTemp Fahrenheit if available
                            String[] fahrenheitParts = title.split("\\( |\\)");
                            if (fahrenheitParts.length >= 3) {
                                String maxTempFahrenheit = fahrenheitParts[2].split("°")[0].trim();
                                maxTempFahrenheit = maxTempFahrenheit.replace("Â", ""); // Remove the special character
                                float maxTemperatureFahrenheit = Float.parseFloat(maxTempFahrenheit);
                                forecast.setMaxTemperatureFahrenheit(Float.isNaN(maxTemperatureFahrenheit) ? 0.0f : maxTemperatureFahrenheit);
                            } else {
                                forecast.setMaxTemperatureFahrenheit(0.0f);
                            }
                        }


                        // Extract day of the week
                        String dayOfWeek = title.split(":")[0].trim();
                        forecast.setDayOfWeek(dayOfWeek);
                        Log.d(TAG, "parseForecastXml: day of week " + dayOfWeek);

                        // Extract weather condition
                        String weatherCondition = title.split(":")[1].split(",")[0].trim();
                        forecast.setWeatherCondition(weatherCondition);

                        if (itemCount == 0) {
                          forecast.setTodayWeatherCondition(weatherCondition);
                            Log.d(TAG, "parseForecastXml: " + weatherCondition);
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
                                    Log.d(TAG, "parseForecastXml: pollution " + value);
                                    break;
                                case "Sunrise":
                                    forecast.setSunrise(parseSunriseSunset(value));
                                break;
                                case "Sunset":
                                    forecast.setSunset(parseSunriseSunset(value));
                                    break;
                            }
                        } Log.d(TAG, "parseForecastXml: sunset " + forecast.getSunrise());
                    }
                }
            } else if (eventType == XmlPullParser.START_TAG && "pubDate".equals(parser.getName())) {
                if (forecast != null && itemCount == 1) {
                    String pubDate = parser.nextText();
                    // Extract date
                    String date = pubDate.split(",")[1].trim();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    try {
                        Date parsedDate = inputFormat.parse(date);
                        baseDate = outputFormat.format(parsedDate);
                        forecast.setDate(baseDate);
                    } catch (ParseException e) {
                        Log.e(TAG, "Error parsing date: " + date, e);
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG && "item".equals(parser.getName())) {
                if (forecast != null) {
                    if (itemCount > 1 && baseDate != null) {
                        // Calculate the date for subsequent forecast items
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                        try {
                            Date parsedDate = dateFormat.parse(baseDate);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(parsedDate);
                            calendar.add(Calendar.DAY_OF_YEAR, itemCount - 1);
                            String date = dateFormat.format(calendar.getTime());
                            forecast.setDate(date);
                        } catch (ParseException e) {
                            Log.e(TAG, "Error parsing date: " + baseDate, e);
                        }
                    }
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

    private static String parseSunriseSunset(String value) {
        // Example value: "05:27 BST"
        String[] parts = value.split(" ");
        if (parts.length == 2) {
            return parts[0]; // Return the time part
        }
        return value; // Return the original value if the format is not as expected
    }
}