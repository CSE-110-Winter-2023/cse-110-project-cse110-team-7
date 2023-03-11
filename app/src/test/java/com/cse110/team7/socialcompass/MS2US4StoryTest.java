package com.cse110.team7.socialcompass;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.database.LabeledLocationDao;
import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.server.LabeledLocationRepository;
import com.cse110.team7.socialcompass.server.ServerAPI;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.LabeledLocationDisplay;
import com.cse110.team7.socialcompass.utils.AngleCalculator;
import com.cse110.team7.socialcompass.utils.DistanceFilter;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@RunWith(RobolectricTestRunner.class)
public class MS2US4StoryTest {

    private static final LabeledLocation testLocation1 = new LabeledLocation.Builder()
            .setLabel("Mom")
            .setLatitude(40)
            .setLongitude(50)
            .build();



    private SocialCompassDatabase socialCompassDatabase;
    private LabeledLocationDao labeledLocationDao;
    private LabeledLocationRepository labeledLocationRepository;

    @Before
    public void init() throws ExecutionException, InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        SocialCompassDatabase.injectTestDatabase(
                Room.inMemoryDatabaseBuilder(context, SocialCompassDatabase.class)
                        .allowMainThreadQueries()
                        .build()
        );

        socialCompassDatabase = SocialCompassDatabase.getInstance(context);
        labeledLocationDao = socialCompassDatabase.getLabeledLocationDao();
        labeledLocationRepository = new LabeledLocationRepository(labeledLocationDao);

        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation1).get();
    }

    @After
    public void destroy() throws ExecutionException, InterruptedException {
        socialCompassDatabase.close();

        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation1).get();
    }

    @Test
    public void US4StoryTest1() throws ExecutionException, InterruptedException {
        var scenario = ActivityScenario.launch(AddFriendActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            activity.getFriendUIDEditText().setText(testLocation1.getPublicCode());
            activity.getFriendUIDEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getAddFriendButton().performClick();
        });

        Assert.assertTrue(labeledLocationDao.selectLabeledLocationWithoutLiveData(
                testLocation1.getPublicCode()).getCoordinate().equals(testLocation1.getCoordinate()));

        testLocation1.setLatitude(20);
        testLocation1.setLongitude(30);
        testLocation1.setUpdatedAt(Instant.now().getEpochSecond());
        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation1).get();

        labeledLocationRepository.syncedSelectLabeledLocation(testLocation1.getPublicCode()).observeForever(new Observer<LabeledLocation>() {
            @Override
            public void onChanged(LabeledLocation labeledLocation) { }
        });

        Assert.assertTrue(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation1.getPublicCode()).getCoordinate().equals(testLocation1.getCoordinate()));

    }

    @Test
    public void US4StoryTest2() throws ExecutionException, InterruptedException {
        var scenario1 = ActivityScenario.launch(AddFriendActivity.class);
        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.moveToState(Lifecycle.State.STARTED);

        scenario1.onActivity(activity -> {
            activity.getFriendUIDEditText().setText(testLocation1.getPublicCode());
            activity.getFriendUIDEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getAddFriendButton().performClick();
        });

        var scenario2 = ActivityScenario.launch(CompassActivity.class);
        scenario2.moveToState(Lifecycle.State.CREATED);
        scenario2.moveToState(Lifecycle.State.STARTED);

        Coordinate currentCoordinate = new Coordinate(10, 20);

        scenario2.onActivity(activity -> {
            LocationService.getInstance().unregisterLocationUpdateListener();
            LocationService.getInstance().setCurrentCoordinate(currentCoordinate);
            OrientationService.getInstance().unregisterSensorEventUpdateListener();
            OrientationService.getInstance().setCurrentOrientation(0);
            activity.getCompass().updateBearingForAll(currentCoordinate);
            var temp = activity.getCompass().getLabeledLocationDisplayMap();

            System.out.println(temp.get(testLocation1.getPublicCode()).getBearing());

            Assert.assertEquals(Double.compare(
                    AngleCalculator.calculateAngle(currentCoordinate, testLocation1.getCoordinate()),
                    Objects.requireNonNull(temp.get(testLocation1.getPublicCode())).getBearing()),
                    0);

            testLocation1.setLatitude(20);
            testLocation1.setLongitude(20);
            testLocation1.setUpdatedAt(Instant.now().getEpochSecond());
            try {
                ServerAPI.getInstance().asyncPutLabeledLocation(testLocation1).get();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            labeledLocationRepository.syncedSelectLabeledLocation(testLocation1.getPublicCode()).observeForever(new Observer<LabeledLocation>() {
                @Override
                public void onChanged(LabeledLocation labeledLocation) { }
            });

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(testLocation1);
            System.out.println(temp.get(testLocation1.getPublicCode()).getLabeledLocation());
            Assert.assertEquals(Double.compare(
                    AngleCalculator.calculateAngle(currentCoordinate, testLocation1.getCoordinate()),
                    Objects.requireNonNull(temp.get(testLocation1.getPublicCode())).getBearing()),
                    0);
        });

    }

}
