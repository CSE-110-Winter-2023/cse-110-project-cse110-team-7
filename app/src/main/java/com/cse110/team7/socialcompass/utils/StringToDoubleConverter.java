package com.cse110.team7.socialcompass.utils;

import androidx.annotation.NonNull;


/**
 * This class converts double string to double value, will return 0 if
 * parse failed
 */
public class StringToDoubleConverter {
    public static double convert(@NonNull String doubleString) {
        try {
            return Double.parseDouble(doubleString);
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            return 0;
        }
    }
}
