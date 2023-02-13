package com.cse110.team7.socialcompass.models;

import androidx.annotation.NonNull;

public class LatLong {
    private double latitude;
    private double longitude;

    public LatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LatLong latLong = (LatLong) obj;
        return Double.compare(latLong.latitude, latitude) == 0 && Double.compare(latLong.longitude, longitude) == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "LatLong{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
