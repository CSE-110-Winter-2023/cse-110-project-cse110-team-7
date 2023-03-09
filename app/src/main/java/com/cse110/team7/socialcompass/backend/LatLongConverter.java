package com.cse110.team7.socialcompass.backend;

import androidx.room.TypeConverter;

import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.utils.StringToDoubleConverter;


/**
 * Apparently it would be much easier to just have two doubles stored in
 * FriendAccount instead of this, but this would work too, LatLong will be stored
 * as latitude,longitude in database
 */
public class LatLongConverter {
    @TypeConverter
    public static LatLong stringToLatLong(String value) {
        if (value == null) return null;

        if(value.charAt(0) == '(' && value.charAt(value.length() - 1) == ')'){
            value = value.substring(1, value.length() - 1);
        }

        String[] latitudeAndLongitude = value.split(",");

        if (latitudeAndLongitude.length != 2) return null;

        double latitude = StringToDoubleConverter.convert(latitudeAndLongitude[0]);
        double longitude = StringToDoubleConverter.convert(latitudeAndLongitude[1]);

        return new LatLong(latitude, longitude);
    }

    @TypeConverter
    public static String latLongToString(LatLong latLong) {
        if (latLong == null) return null;

        return latLong.toString();
    }
}
