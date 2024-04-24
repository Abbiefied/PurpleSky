package org.me.gcu.adekunle_ganiyat_s2110996.utils;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.me.gcu.adekunle_ganiyat_s2110996.data.models.AirQualityData;

public class AirQualityMarker implements ClusterItem {
    private LatLng mPosition;
    private AirQualityData mAirQualityData;
    private Bitmap mMarkerIcon;

    public AirQualityMarker(LatLng position, AirQualityData airQualityData, Bitmap markerIcon) {
        mPosition = position;
        mAirQualityData = airQualityData;
        mMarkerIcon = markerIcon;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return "Air Quality";
    }

    @Override
    public String getSnippet() {
        return mAirQualityData.getAirQualityIndex();
    }

    public AirQualityData getAirQualityData() {
        return mAirQualityData;
    }

    public Bitmap getMarkerIcon() {
        return mMarkerIcon;
    }
}