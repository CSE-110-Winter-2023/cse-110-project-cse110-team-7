package com.cse110.team7.socialcompass.services;

import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.models.Coordinate;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * Observes location updates from user and broadcast to all observers
 */
public class LocationService {
    private static final int SECS_PER_MIN = 60;
    private static final int MIN_PER_HOUR = 60;
    public static final int MILIS_PER_SEC = 1000;
    private static LocationService INSTANCE = null;

    private final MutableLiveData<Coordinate> currentCoordinate;
    private final LocationListener locationUpdateListener;
    private LocationManager locationManager;
    private boolean isListenerRegistered;

    private MutableLiveData<Date> lastSignalTime;
    private ScheduledFuture<?> future;
    private MutableLiveData<String> formattedLastSignalTime;

    private LocationService() {
        this.currentCoordinate = new MutableLiveData<>();
        this.lastSignalTime = new MutableLiveData<>();
        this.formattedLastSignalTime = new MutableLiveData<>();
        this.locationUpdateListener = location -> {
            var currentCoordinate = this.currentCoordinate.getValue();
            var nextCoordinate = new Coordinate(location.getLatitude(), location.getLongitude());

            if (currentCoordinate == null || !nextCoordinate.equals(currentCoordinate)) {
                Log.d(LocationService.class.getName(), "update current coordinate to " + nextCoordinate);

                this.currentCoordinate.postValue(nextCoordinate);
            }

            lastSignalTime.postValue(Calendar.getInstance().getTime());

        };

        this.trackGPSStatus();
        this.isListenerRegistered = false;
    }

    public static LocationService getInstance() {
        if (INSTANCE == null) INSTANCE = new LocationService();
        return INSTANCE;
    }

    /**
     * Register the location update listener to start receiving location updates from sensor
     *
     * @throws SecurityException throws if application does not have the required location permission
     */
    public void registerLocationUpdateListener() throws SecurityException {
        if (isListenerRegistered) {
            Log.w(LocationService.class.getName(), "location update listener is already registered when registering location update listener");
            return;
        }

        if (this.locationManager == null) {
            Log.w(LocationService.class.getName(), "location manager is null when registering location update listener");
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0, 0,
                locationUpdateListener
        );

        isListenerRegistered = true;

        Log.i(LocationService.class.getName(), "location update listener registered");
    }

    /**
     * Unregister the location update listener to stop receiving location updates from sensor
     */
    public void unregisterLocationUpdateListener() {
        if (!isListenerRegistered) {
            Log.w(LocationService.class.getName(), "location update listener is not registered when unregistering location update listener");
            return;
        }

        if (this.locationManager == null) {
            Log.w(LocationService.class.getName(), "location manager is null when unregistering location update listener");
            return;
        }

        locationManager.removeUpdates(locationUpdateListener);

        isListenerRegistered = false;

        Log.i(LocationService.class.getName(), "location update listener unregistered");
    }

    /**
     * Broadcast the given location immediately
     *
     * @param coordinate the location to be sent to all observers
     */
    public void setCurrentCoordinate(@NonNull Coordinate coordinate) {
        this.currentCoordinate.setValue(coordinate);
    }

    /**
     * Get the location subject
     *
     * @return the location subject
     */
    public MutableLiveData<Coordinate> getCurrentCoordinate() {
        return currentCoordinate;
    }

    public void setLocationManager(@NonNull LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    /**
     * If there isn't GPS signal gets the time signal was lost
     *
     * @return time GPS signal was lost
     */
    public String parseLastSignalTime() {
        String formattedString;
        long milisDifference = Calendar.getInstance().getTime().getTime()- lastSignalTime.getValue().getTime();
        long minDifference = milisDifference / MILIS_PER_SEC /SECS_PER_MIN;
        if (minDifference < SECS_PER_MIN) {
            formattedString = minDifference+"m";
            return formattedString;
        }
        long hourDifference = minDifference /MIN_PER_HOUR;
        formattedString = hourDifference+"h";
        return formattedString;
    }

    public void trackGPSStatus() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        this.future = executor.scheduleAtFixedRate(() -> {
            if (lastSignalTime.getValue() == null) return;
            if (lastSignalTime.getValue().getTime() + SECS_PER_MIN * MILIS_PER_SEC < Calendar.getInstance().getTime().getTime()) {
                formattedLastSignalTime.postValue(parseLastSignalTime());
            } else {
                formattedLastSignalTime.postValue("");
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public MutableLiveData<String> getFormattedLastSignalTime() {
        return formattedLastSignalTime;
    }

}
