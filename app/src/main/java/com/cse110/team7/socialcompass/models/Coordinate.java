package com.cse110.team7.socialcompass.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Represents a coordinate on the map
 */
public class Coordinate {
    /**
     * latitude of the coordinate
     */
    public final double latitude;
    /**
     * longitude of the coordinate
     */
    public final double longitude;

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Determine whether the given coordinate is the same as this coordinate
     *
     * @param coordinate the coordinate to be compared with this
     * @return whether the given coordinate is the same as this coordinate
     */
    public boolean equals(@Nullable Coordinate coordinate) {
        if (coordinate == null) return false;

        return Double.compare(latitude, coordinate.latitude) == 0
                && Double.compare(longitude, coordinate.longitude) == 0;
    }

    @NonNull
    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
