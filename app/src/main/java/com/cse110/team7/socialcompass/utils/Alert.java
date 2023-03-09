package com.cse110.team7.socialcompass.utils;

import android.app.Activity;
import android.app.AlertDialog;

import androidx.annotation.NonNull;


/**
 * Display alert in a given activity
 */
public class Alert {
    /**
     * Display a new alert in the given activity with the given message
     *
     * @param activity the activity which the alert will be shown
     * @param message the message of the alert
     */
    public static void show(@NonNull Activity activity, @NonNull String message) {
        new AlertDialog.Builder(activity)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("Ok", ((dialog, which) -> dialog.cancel()))
                .setCancelable(true)
                .create()
                .show();
    }
}
