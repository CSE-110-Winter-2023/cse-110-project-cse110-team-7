package com.cse110.team7.socialcompass.utils;

import androidx.annotation.NonNull;

import com.cse110.team7.socialcompass.models.Coordinate;


/**
 * Calculate the distance between two coordinates on map
 */
public class DistanceCalculator {
    private static final int EARTH_RADIUS = 6371;

    /**
     * Calculate the distance between two coordinates on map
     *
     * @param coordinate the first coordinate
     * @param otherCoordinate the second coordinate
     * @return the distance between the given coordinates on map in miles
     */
    public static double calculateDistance(@NonNull Coordinate coordinate, @NonNull Coordinate otherCoordinate) {
        double latitudeDifference = Math.toRadians(otherCoordinate.latitude - coordinate.latitude);
        double longitudeDifference = Math.toRadians(otherCoordinate.longitude - coordinate.longitude);

        double a = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                + Math.cos(Math.toRadians(otherCoordinate.latitude)) * Math.cos(Math.toRadians(coordinate.latitude))
                * Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c * .621;
    }
}
