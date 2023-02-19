package com.cse110.team7.socialcompass;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.backend.HouseDao;
import com.cse110.team7.socialcompass.backend.HouseDatabase;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

@RunWith(RobolectricTestRunner.class)


public class US5StoryTest {
    private HouseDao houseDao;
    private HouseDatabase houseDatabase;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        houseDatabase = Room.inMemoryDatabaseBuilder(context, HouseDatabase.class)
                .allowMainThreadQueries()
                .build();

        HouseDatabase.injectTestDatabase(houseDatabase);

        houseDao = houseDatabase.getHouseDao();
    }

    @After
    public void closeDatabase() {
        houseDatabase.close();
    }

    @Test
    public void orientationInput() {
        float currentLat = 32.88190048420199f;
        float currentLong = -117.23782850478656f;

        LatLong currentLocation = new LatLong(currentLat, currentLong);
        float parentLat = 32.883578357002115f;
        float parentLong = -117.23500480616329f;
        LatLong parentLocation = new LatLong(parentLat, parentLong);

        float mockOrientation = 49.5f;

        ActivityScenario<MainActivity> mainScenario = ActivityScenario.launch(MainActivity.class);
        mainScenario.moveToState(Lifecycle.State.CREATED);
        mainScenario.moveToState(Lifecycle.State.STARTED);

        House parentHouse = new House("parents", new LatLong(parentLat, parentLong));
        houseDao.insertHouse(parentHouse);

        mainScenario.onActivity(mainActivity -> {

            Intent intent = new Intent(mainActivity, CompassActivity.class);

            intent.putExtra("orientation", mockOrientation);
            ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(intent);
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.moveToState(Lifecycle.State.STARTED);

            scenario.onActivity(activity -> {
                LocationService.getInstance().unregisterLocationUpdateListener();
                LocationService.getInstance().setUserLocation(currentLocation);

                var layoutParams = (ConstraintLayout.LayoutParams) activity.getCompass().getElements().get(1).getDotView().getLayoutParams();
                float appAngle = layoutParams.circleAngle;
                float parentBearing = AngleCalculator.calculateAngle(currentLocation, parentLocation);
                assertEquals(0, Float.compare(appAngle, parentBearing - mockOrientation));

            });
            scenario.close();
        });
    }

}