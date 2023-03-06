package com.cse110.team7.socialcompass.utils;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

public class DistanceCalculator {

   public static float calculateDistance(LatLong currentLocation, LatLong friendLocation) {
      final int R = 6371; // Radius of the earth

      double dLa = Math.toRadians(friendLocation.getLatitude() - currentLocation.getLatitude());
      double dLo = Math.toRadians(friendLocation.getLongitude() - currentLocation.getLongitude());
      double a = Math.sin(dLa / 2) * Math.sin(dLa / 2)
              + Math.cos(Math.toRadians(friendLocation.getLatitude())) * Math.cos(Math.toRadians(currentLocation.getLatitude()))
              * Math.sin(dLo / 2) * Math.sin(dLo / 2);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      double distance = R * c * 1000; // convert to meters

      return (float) distance;
   }

   public static float calculateDistance(LatLong currentLocation, FriendAccount friendAccount) {
      return calculateDistance(currentLocation, friendAccount.getLocation());
   }
}
