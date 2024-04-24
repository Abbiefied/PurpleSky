package org.me.gcu.adekunle_ganiyat_s2110996.data.sources;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.CurrentWeather;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Forecast;
import org.me.gcu.adekunle_ganiyat_s2110996.data.models.Location;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.AppExecutors;
import org.me.gcu.adekunle_ganiyat_s2110996.utils.NetworkUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkDataSource {

    private static final String TAG = NetworkDataSource.class.getSimpleName();

    public interface WeatherCallback<T> {
        void onSuccess(T data);
        void onFailure(String message);
    }

    public void fetchCurrentWeather(String locationId, WeatherCallback<CurrentWeather> callback) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                String url = NetworkUtils.buildCurrentWeatherUrl(locationId);
                Log.d(TAG, "Fetching current weather data from: " + url);
                String response = NetworkUtils.getResponseFromHttpUrl(url);
                Log.d(TAG, "Received current weather response: " + response);

                if (response.startsWith("{")) {
                    // JSON response
                    Log.e(TAG, "Unexpected JSON response: " + response);
                    AppExecutors.getInstance().mainThread().execute(() -> callback.onFailure("Unexpected JSON response"));
                } else {
                    // XML response
                    CurrentWeather currentWeather = NetworkUtils.parseCurrentWeatherXml(response);
                    Log.d(TAG, "Parsed current weather data: " + currentWeather);
                    AppExecutors.getInstance().mainThread().execute(() -> callback.onSuccess(currentWeather));
                }
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error fetching current weather: " + e.getMessage());
                AppExecutors.getInstance().mainThread().execute(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    public void fetchWeatherForecast(String locationId, WeatherCallback<List<Forecast>> callback) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                String url = NetworkUtils.buildForecastUrl(locationId);
                Log.d(TAG, "Fetching forecast weather data from: " + url);
                String xmlResponse = NetworkUtils.getResponseFromHttpUrl(url);
                Log.d(TAG, "Received forecast weather XML data: " + xmlResponse); // Log the raw XML data
                List<Forecast> forecastList = NetworkUtils.parseForecastXml(xmlResponse);
                Log.d(TAG, "Received parsed forecast weather data: " + forecastList); // Log the parsed data
                AppExecutors.getInstance().mainThread().execute(() -> callback.onSuccess(forecastList));
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error fetching weather forecast: " + e.getMessage());
                AppExecutors.getInstance().mainThread().execute(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    public void fetchSearchSuggestions(String query, SearchCallback<List<Location>> callback) {
        // Simulate network call to fetch search suggestions
        List<Location> suggestions = new ArrayList<>();
        suggestions.add(new Location("Glasgow"));
        suggestions.add(new Location("Bangladesh"));
        suggestions.add(new Location("London"));
        suggestions.add(new Location("New York"));
        suggestions.add(new Location("Mauritius"));
        suggestions.add(new Location("Oman"));

        // Filter suggestions based on the query
        List<Location> filteredSuggestions = new ArrayList<>();
        for (Location location : suggestions) {
            if (location.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredSuggestions.add(location);
            }
        }

        if (filteredSuggestions.isEmpty()) {
            callback.onFailure("No suggestions found");
        } else {
            callback.onSuccess(filteredSuggestions);
        }
    }

    public void fetchAirQualityData(double latitude, double longitude, WeatherCallback<AirQualityData> callback) {
        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                String url = "https://airquality.googleapis.com/v1/currentConditions:lookup?key=AIzaSyCfy_LipUHZ2iIupLckBFUSU0n9dNncs4o";
                String requestBody = constructAirQualityRequestBody(latitude, longitude);
                String response = NetworkUtils.postRequestToHttpUrl(url, requestBody);
                Log.d(TAG, "fetchAirQualityData: " + response);
                AirQualityData airQualityData = parseAirQualityJson(response);
                AppExecutors.getInstance().mainThread().execute(() -> callback.onSuccess(airQualityData));
            } catch (IOException e) {
                Log.e(TAG, "Error fetching air quality data: " + e.getMessage());
                AppExecutors.getInstance().mainThread().execute(() -> callback.onFailure(e.getMessage()));
            }
        });
    }

    private String constructAirQualityRequestBody(double latitude, double longitude) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("universalAqi", true);

            JSONObject location = new JSONObject();
            location.put("latitude", latitude);
            location.put("longitude", longitude);
            requestBody.put("location", location);

            JSONArray extraComputations = new JSONArray();
            extraComputations.put("HEALTH_RECOMMENDATIONS");
            extraComputations.put("DOMINANT_POLLUTANT_CONCENTRATION");
            extraComputations.put("POLLUTANT_CONCENTRATION");
            extraComputations.put("LOCAL_AQI");
            extraComputations.put("POLLUTANT_ADDITIONAL_INFO");
            requestBody.put("extraComputations", extraComputations);

            requestBody.put("languageCode", "en");
        } catch (JSONException e) {
            Log.e(TAG, "Error constructing air quality request body: " + e.getMessage());
        }
        return requestBody.toString();
    }


    private AirQualityData parseAirQualityJson(String json) {
        AirQualityData airQualityData = new AirQualityData();

        try {
            JSONObject jsonObject = new JSONObject(json);

            airQualityData.setDateTime(jsonObject.optString("dateTime"));
            airQualityData.setRegionCode(jsonObject.optString("regionCode"));

            JSONArray indexesArray = jsonObject.optJSONArray("indexes");
            List<AirQualityData.AirQualityIndex> indexes = new ArrayList<>();
            for (int i = 0; i < indexesArray.length(); i++) {
                JSONObject indexObject = indexesArray.getJSONObject(i);
                AirQualityData.AirQualityIndex index = new AirQualityData.AirQualityIndex();
                index.setCode(indexObject.optString("code"));
                index.setDisplayName(indexObject.optString("displayName"));
                index.setAqi(indexObject.optInt("aqi"));
                index.setAqiDisplay(indexObject.optString("aqiDisplay"));
                index.setCategory(indexObject.optString("category"));
                index.setDominantPollutant(indexObject.optString("dominantPollutant"));

                JSONObject colorObject = indexObject.optJSONObject("color");
                if (colorObject != null) {
                    AirQualityData.Color color = new AirQualityData.Color();
                    color.setRed(colorObject.optDouble("red"));
                    color.setGreen(colorObject.optDouble("green"));
                    color.setBlue(colorObject.optDouble("blue"));
                    index.setColor(color);
                }

                indexes.add(index);
            }
            airQualityData.setIndexes(indexes);

            JSONArray pollutantsArray = jsonObject.optJSONArray("pollutants");
            List<AirQualityData.Pollutant> pollutants = new ArrayList<>();
            for (int i = 0; i < pollutantsArray.length(); i++) {
                JSONObject pollutantObject = pollutantsArray.getJSONObject(i);
                AirQualityData.Pollutant pollutant = new AirQualityData.Pollutant();
                pollutant.setCode(pollutantObject.optString("code"));
                pollutant.setDisplayName(pollutantObject.optString("displayName"));
                pollutant.setFullName(pollutantObject.optString("fullName"));

                JSONObject concentrationObject = pollutantObject.optJSONObject("concentration");
                if (concentrationObject != null) {
                    AirQualityData.Concentration concentration = new AirQualityData.Concentration();
                    concentration.setValue(concentrationObject.optDouble("value"));
                    concentration.setUnits(concentrationObject.optString("units"));
                    pollutant.setConcentration(concentration);
                }

                JSONObject additionalInfoObject = pollutantObject.optJSONObject("additionalInfo");
                if (additionalInfoObject != null) {
                    AirQualityData.AdditionalInfo additionalInfo = new AirQualityData.AdditionalInfo();
                    additionalInfo.setSources(additionalInfoObject.optString("sources"));
                    additionalInfo.setEffects(additionalInfoObject.optString("effects"));
                    pollutant.setAdditionalInfo(additionalInfo);
                }

                pollutants.add(pollutant);
            }
            airQualityData.setPollutants(pollutants);

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing air quality JSON: " + e.getMessage());
        }

        return airQualityData;
    }

    public interface SearchCallback<T> {
        void onSuccess(T data);
        void onFailure(String message);
    }
}