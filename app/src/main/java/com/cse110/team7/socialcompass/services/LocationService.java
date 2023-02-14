package com.cse110.team7.socialcompass.services;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cse110.team7.socialcompass.models.LatLong;

public class LocationService {
    /**
     * this field is the only available instance of this class for the
     * entire lifecycle of the application, access it by the getter function
     */
    private static final LocationService INSTANCE = new LocationService();

    /**
     * Get the instance of the class
     * @return the only available instance of this class
     */
    public static LocationService getInstance() {
        return INSTANCE;
    }

    /**
     * this field is an active system location manager,
     * which will be obtained by an active activity elsewhere
     */
    private LocationManager locationManager;
    /**
     * this field take cares of the lifecycle, observers
     * will only be called when the activity it belongs is active
     */
    private final MutableLiveData<LatLong> userLocation;
    /**
     * this field is a listener instance, used to remove
     * listeners from system service, will be called when a
     * new location value is available
     */
    private final LocationListener locationListener;

    private LocationService() {
        this.userLocation = new MutableLiveData<>();
        // note that postValue is handled by the main thread, so there will be
        // a short period of time until the observers are called
        this.locationListener = location -> userLocation.postValue(new LatLong(location.getLatitude(), location.getLongitude()));
    }

    /**
     * Call this method to start receiving location updates from system location services
     * @param activity The activity that requires the update
     */
    public void registerLocationUpdateListener(Activity activity) {
        if (this.locationManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Location Manager is null when registering location update listener");
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        // request updates and register callback
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                this.locationListener
        );
    }

    /**
     * Call this method to stop receiving location updates from system location services
     */
    public void unregisterLocationUpdateListener() {
        if (this.locationManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Location Manager is null when unregistering location update listener");
        }

        this.locationManager.removeUpdates(this.locationListener);
    }

    /**
     * Get the live data instance to add observers on it
     * @return the live data instance holding the current location
     */
    public LiveData<LatLong> getUserLocation() {
        return this.userLocation;
    }

    /**
     * Set the system location service, must be called before registering for location updates
     * @param locationManager System location service obtained from active activity
     */
    public void setLocationManager(@NonNull LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    /**
     * Directly set the current location and trigger update immediately, this
     * can be used for testing
     * @param location the new location
     */
    public void setUserLocation(@NonNull LatLong location) {
        // note that setValue is handled by the current thread, so this
        // will be executed immediately and all observers will be notified
        this.userLocation.setValue(location);
    }
}
