package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)

public class US2StoryTest {
    private FriendAccountDao friendAccountDao;
    private FriendDatabase friendDatabase;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        friendDatabase = Room.inMemoryDatabaseBuilder(context, FriendDatabase.class)
                .allowMainThreadQueries()
                .build();

        FriendDatabase.injectTestDatabase(friendDatabase);

        friendAccountDao = friendDatabase.getFriendDao();
    }

    @After
    public void closeDatabase() {
        friendDatabase.close();
    }

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


        FriendAccount parentFriendAccount = new FriendAccount("parents", new LatLong(parentLat, parentLong));
        friendAccountDao.insertFriend(parentFriendAccount);

        mainScenario.onActivity(mainActivity -> {
            Intent intent = new Intent(mainActivity, CompassActivity.class);



            ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(intent);
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.moveToState(Lifecycle.State.STARTED);

            scenario.onActivity(activity -> {
                LocationService.getInstance().unregisterLocationUpdateListener();
                OrientationService.getInstance().unregisterSensorEventListener();

                LocationService.getInstance().setUserLocation(currentLocation);
                OrientationService.getInstance().setAzimuth(orientation);

                var layoutParams = (ConstraintLayout.LayoutParams) activity.getCompass().getElements().get(1).getDotView().getLayoutParams();
                float appAngle = layoutParams.circleAngle;
                float realAngle = AngleCalculator.calculateAngle(currentLocation, parentLocation) - orientation;

                assertEquals(0, Double.compare(appAngle, realAngle));
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
            FriendAccount parentFriendAccount = new FriendAccount("parents", new LatLong(parentLat, parentLong));
            friendAccountDao.insertFriend(parentFriendAccount);

            ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(intent);
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.moveToState(Lifecycle.State.STARTED);

            scenario.onActivity(activity -> {
                LocationService.getInstance().unregisterLocationUpdateListener();
                OrientationService.getInstance().unregisterSensorEventListener();

                LocationService.getInstance().setUserLocation(currentLocation);
                OrientationService.getInstance().setAzimuth(orientation);

                var layoutParams = (ConstraintLayout.LayoutParams) activity.getCompass().getElements().get(1).getDotView().getLayoutParams();
                float appAngle = layoutParams.circleAngle;
                float realAngle = AngleCalculator.calculateAngle(currentLocation, parentLocation) - orientation;

                assertEquals(Double.compare(appAngle, realAngle), 0);
            });
            scenario.close();
        });
    }

}
