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
    private static final LocationService INSTANCE = new LocationService();

    public static LocationService getInstance() {
        return INSTANCE;
    }

    private LocationManager locationManager;
    private final MutableLiveData<LatLong> userLocation;
    private final LocationListener locationListener;

    private LocationService() {
        this.userLocation = new MutableLiveData<>();
        this.locationListener = location -> userLocation.postValue(new LatLong(location.getLatitude(), location.getLongitude()));
    }

    public void registerLocationUpdateListener(Activity activity) {
        if (this.locationManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Location Manager is null when registering location update listener");
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                this.locationListener
        );
    }

    public void unregisterLocationUpdateListener() {
        if (this.locationManager == null) {
            throw new NullPointerException(this.getClass().getName() + ": Location Manager is null when unregistering location update listener");
        }

        this.locationManager.removeUpdates(this.locationListener);
    }

    public LiveData<LatLong> getUserLocation() {
        return this.userLocation;
    }

    public void setLocationManager(@NonNull LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public void setUserLocation(@NonNull LatLong location) {
        this.userLocation.setValue(location);
    }
}
