package com.cse110.team7.socialcompass.utils;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import java.util.List;
import java.util.stream.Collectors;

public class DistanceFilter {
    public static boolean isFriendInRange(
            FriendAccount friendAccount,
            LatLong currentLocation,
            double minDistance,
            double maxDistance
    ) {
        double friendDistance = DistanceCalculator.calculateDistance(currentLocation, friendAccount.getLocation());
        return Double.compare(maxDistance, friendDistance) > 0 && Double.compare(minDistance, friendDistance) <= 0;
    }

    public static boolean isFriendFurtherThanMaxDistance(
            FriendAccount friendAccount,
            LatLong currentLocation,
            double maxDistance
    ) {
        double friendDistance = DistanceCalculator.calculateDistance(currentLocation, friendAccount.getLocation());
        return Double.compare(maxDistance, friendDistance) <= 0;
    }

    /**
     * Filter friends by distance range, the result friends are within the range [minDistance, maxDistance)
     * @param friendAccounts all friends to be filtered
     * @param currentLocation current location of the user
     * @param minDistance min distance in the range
     * @param maxDistance max distance in the range
     * @return filtered friends
     */
    public static List<FriendAccount> filterFriends(
            List<FriendAccount> friendAccounts,
            LatLong currentLocation,
            double minDistance,
            double maxDistance
    ) {
        return friendAccounts.stream().filter(friendAccount -> {
            double friendDistance = DistanceCalculator.calculateDistance(currentLocation, friendAccount);
            return Double.compare(maxDistance, friendDistance) > 0 && Double.compare(minDistance, friendDistance) <= 0;
        }).collect(Collectors.toList());
    }
}
