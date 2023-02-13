package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)

public class US2StoryTest {

    @Test
    public void US2testCase1() {
        float currentLat = 0;
        float currentLong = 0;
        float orientation = 0;

        LatLong currentLocation = new LatLong(currentLat, currentLong);

        float parentLat = 0;
        float parentLong = 10;

        LatLong parentLocation = new LatLong(parentLat, parentLong);

        ActivityScenario<MainActivity> mainScenario = ActivityScenario.launch(MainActivity.class);
        mainScenario.moveToState(Lifecycle.State.CREATED);
        mainScenario.moveToState(Lifecycle.State.STARTED);

        mainScenario.onActivity(mainActivity -> {
            Intent intent = new Intent(mainActivity, CompassActivity.class);
            intent.putExtra("lat", parentLat);
            intent.putExtra("long", parentLong);

            ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(intent);
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.moveToState(Lifecycle.State.STARTED);

            scenario.onActivity(activity -> {
                LocationService.getInstance().unregisterLocationUpdateListener();
                OrientationService.getInstance().unregisterSensorEventListener();

                LocationService.getInstance().setUserLocation(currentLocation);
                OrientationService.getInstance().setAzimuth(orientation);

                var layoutParams = (ConstraintLayout.LayoutParams) activity.compass.getAllElements().get(0).getDotView().getLayoutParams();
                float appAngle = layoutParams.circleAngle;
                float realAngle = AngleCalculator.calculateAngle(currentLocation, parentLocation) - orientation;

                System.out.println("App: " + appAngle);
                System.out.println("Real: " + realAngle);

                assertTrue(Double.compare(appAngle, realAngle) == 0);
            });
            scenario.close();
        });
    }

    @Test
    public void US2testCase2() {
        float currentLat = 32.88190048420199f;
        float currentLong = -117.23782850478656f;
        float orientation = 49.5f;

        LatLong currentLocation = new LatLong(currentLat, currentLong);

        float parentLat = 32.883578357002115f;
        float parentLong = -117.23500480616329f;

        LatLong parentLocation = new LatLong(parentLat, parentLong);

        ActivityScenario<MainActivity> mainScenario = ActivityScenario.launch(MainActivity.class);
        mainScenario.moveToState(Lifecycle.State.CREATED);
        mainScenario.moveToState(Lifecycle.State.STARTED);

        mainScenario.onActivity(mainActivity -> {
            Intent intent = new Intent(mainActivity, CompassActivity.class);
            intent.putExtra("lat", parentLat);
            intent.putExtra("long", parentLong);

            ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(intent);
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.moveToState(Lifecycle.State.STARTED);

            scenario.onActivity(activity -> {
                LocationService.getInstance().unregisterLocationUpdateListener();
                OrientationService.getInstance().unregisterSensorEventListener();

                LocationService.getInstance().setUserLocation(currentLocation);
                OrientationService.getInstance().setAzimuth(orientation);

                var layoutParams = (ConstraintLayout.LayoutParams) activity.compass.getAllElements().get(0).getDotView().getLayoutParams();
                float appAngle = layoutParams.circleAngle;
                float realAngle = AngleCalculator.calculateAngle(currentLocation, parentLocation) - orientation;

                System.out.println("App: " + appAngle);
                System.out.println("Real: " + realAngle);

                assertTrue(Double.compare(appAngle, realAngle) == 0);
            });
            scenario.close();
        });
    }

}
