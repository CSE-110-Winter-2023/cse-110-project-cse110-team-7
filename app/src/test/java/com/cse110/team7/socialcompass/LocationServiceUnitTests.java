package com.cse110.team7.socialcompass;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import com.cse110.team7.socialcompass.services.LocationService;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

@RunWith(RobolectricTestRunner.class)
public class LocationServiceUnitTests   {

      @Test
      public void testLocationService(){
         var scenario = ActivityScenario.launch(CompassActivity.class);
         scenario.moveToState(Lifecycle.State.CREATED);
         scenario.moveToState(Lifecycle.State.STARTED);

         scenario.onActivity(activity -> {
             // check that if location manager is null, a nullpointer exception is thrown
             LocationService locationService = LocationService.getInstance();
             LocationManager locationManager = null;
             NullPointerException thrown = assertThrows(
                     NullPointerException.class,
                     () -> {
                        locationService.setLocationManager(locationManager);
                        locationService.registerLocationUpdateListener(activity);
                     });

             NullPointerException thrown2 = assertThrows(
                     NullPointerException.class,
                     () -> {
                         locationService.setLocationManager(locationManager);
                         locationService.unregisterLocationUpdateListener();
                     });

             // check that location permission is not granted
             boolean checkPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                     && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
             assertTrue(checkPermission);

         });
      }

}

