package com.cse110.team7.socialcompass.utils;


import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.models.LabeledLocation;


/**
 * Determine the relationship the distance of a given location and a given range
 */
public class DistanceFilter {
    /**
     * Determine whether the given location lies within the given range around user location,
     * the range is [minDistance, maxDistance)
     *
     * @param labeledLocation the given location
     * @param currentCoordinate user location
     * @param minDistance minimum distance in range
     * @param maxDistance maximum distance in range
     * @return whether the location is in the given range  around user location or not
     */
    public static boolean isLabeledLocationInRange(
            LabeledLocation labeledLocation,
            Coordinate currentCoordinate,
            double minDistance,
            double maxDistance
    ) {
        double friendDistance = DistanceCalculator.calculateDistance(currentCoordinate, labeledLocation.getCoordinate());
        return Double.compare(maxDistance, friendDistance) > 0
                && Double.compare(minDistance, friendDistance) <= 0;
    }

    /**
     * Determine whether the given location is farther than maximum distance in range
     * @param labeledLocation the given location
     * @param currentCoordinate user location
     * @param maxDistance maximum distance in range
     * @return whether the given location is farther than maximum distance in range
     */
    public static boolean isLabeledLocationFartherThanMaxDistance(
            LabeledLocation labeledLocation,
            Coordinate currentCoordinate,
            double maxDistance
    ) {
        double friendDistance = DistanceCalculator.calculateDistance(currentCoordinate, labeledLocation.getCoordinate());
        return Double.compare(maxDistance, friendDistance) <= 0;
    }
}
