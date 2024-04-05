package org.me.gcu.adekunle_ganiyat_s2110996.data.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Location {
    private String name;
    private int id;
    private double latitude;
    private double longitude;

    private static final Map<String, Integer> locationMap = new HashMap<>();
    private static String defaultLocationId;

    static {
        locationMap.put("Glasgow", 2648579);
        locationMap.put("London", 2643743);
        locationMap.put("New York", 5128581);
        locationMap.put("Oman", 287286);
        locationMap.put("Mauritius", 934154);
        locationMap.put("Bangladesh", 1185241);

        defaultLocationId = "2648579"; // Set default location ID (e.g., Glasgow)
    }

    public Location(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public Location(String name, int id, double latitude, double longitude) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static List<Location> getPopularLocations() {
        List<Location> popularLocations = new ArrayList<>();
        popularLocations.add(new Location("Glasgow", 2648579, 55.8642, -4.2518));
        popularLocations.add(new Location("London", 2643743, 51.5074, -0.1278));
        popularLocations.add(new Location("New York", 5128581, 40.7128, -74.0060));
        popularLocations.add(new Location("Oman", 287286, 21.5126, 55.9232));
        popularLocations.add(new Location("Mauritius", 934154, -20.3484, 57.5522));
        popularLocations.add(new Location("Bangladesh", 1185241, 23.8103, 90.4125));
        return popularLocations;
    }

    public static String getDefaultLocationId() {
        return defaultLocationId;
    }

    public static void setDefaultLocationId(String locationId) {
        defaultLocationId = locationId;
    }

    public static String getDefaultLocationName() {
        for (Map.Entry<String, Integer> entry : locationMap.entrySet()) {
            if (entry.getValue().toString().equals(defaultLocationId)) {
                return entry.getKey();
            }
        }
        return "";
    }

    public static String getLocationIdByName(String locationName) {
        for (Map.Entry<String, Integer> entry : locationMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(locationName)) {
                return String.valueOf(entry.getValue());
            }
        }
        return null;
    }

    public static Location getLocationById(String locationId) {
        for (Location location : getPopularLocations()) {
            if (String.valueOf(location.getId()).equals(locationId)) {
                return location;
            }
        }
        return null;
    }
}