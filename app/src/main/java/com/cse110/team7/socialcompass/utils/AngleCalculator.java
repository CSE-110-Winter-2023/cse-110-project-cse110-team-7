package com.cse110.team7.socialcompass.utils;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;

public class AngleCalculator {

    public static float calculateAngle(LatLong currentLocation, LatLong houseLocation) {
        double latC = Math.toRadians(currentLocation.getLatitude());
        double latH = Math.toRadians(houseLocation.getLatitude());
        double dL = Math.toRadians(houseLocation.getLongitude() - currentLocation.getLongitude());
        double x = Math.cos(latH) * Math.sin(dL);
        double y = Math.cos(latC) * Math.sin(latH) - Math.sin(latC) * Math.cos(latH) * Math.cos(dL);
        return (float) ((Math.toDegrees(Math.atan2(x, y)) + 360) % 360);
    }

    public static float calculateAngle(LatLong currentLocation, House house) {
        return  calculateAngle(currentLocation, house.getLocation());
    }

}
