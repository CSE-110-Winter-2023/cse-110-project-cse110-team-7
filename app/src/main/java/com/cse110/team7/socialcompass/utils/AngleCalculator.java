package com.cse110.team7.socialcompass.utils;

import androidx.annotation.NonNull;

import com.cse110.team7.socialcompass.models.Coordinate;


/**
 * Calculate angle between two coordinates on map
 */
public class AngleCalculator {
    /**
     * Calculate the angle between two coordinates on map
     *
     * @param coordinate the first coordinate
     * @param otherCoordinate the second coordinate
     * @return the angle between the given coordinates on map
     */
    public static double calculateAngle(@NonNull Coordinate coordinate, @NonNull Coordinate otherCoordinate) {
        double currentLatitude = Math.toRadians(coordinate.latitude);
        double otherLatitude = Math.toRadians(otherCoordinate.latitude);
        double longitudeDifference = Math.toRadians(otherCoordinate.longitude - coordinate.longitude);
        double x = Math.cos(otherLatitude) * Math.sin(longitudeDifference);
        double y = Math.cos(currentLatitude) * Math.sin(otherLatitude) - Math.sin(currentLatitude) * Math.cos(otherLatitude) * Math.cos(longitudeDifference);
        return Math.toDegrees((Math.atan2(x, y) + 2 * Math.PI) % (2 * Math.PI));
    }
}
