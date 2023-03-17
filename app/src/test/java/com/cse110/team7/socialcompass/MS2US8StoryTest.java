package com.cse110.team7.socialcompass;


import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.LabeledLocationDisplay;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RunWith(RobolectricTestRunner.class)
public class MS2US8StoryTest {

    @Before
    public void init() throws ExecutionException, InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        SocialCompassDatabase.injectTestDatabase(
                Room.inMemoryDatabaseBuilder(context, SocialCompassDatabase.class)
                        .allowMainThreadQueries()
                        .build()
        );
    }
    
    @Test
    public void US8StoryTest() {
        LabeledLocation test1 = new LabeledLocation.Builder()
                .setPublicCode(UUID.randomUUID().toString())
                .setPrivateCode(UUID.randomUUID().toString())
                .setLabel("Youka")
                .setLatitude(37.4)
                .setLongitude(-122.098)
                .build();
        LabeledLocation test2 = new LabeledLocation.Builder()
                .setPublicCode(UUID.randomUUID().toString())
                .setPrivateCode(UUID.randomUUID().toString())
                .setLabel("Noa")
                .setLatitude(37.4)
                .setLongitude(-122.100)
                .build();
        LabeledLocation test3 = new LabeledLocation.Builder()
                .setPublicCode(UUID.randomUUID().toString())
                .setPrivateCode(UUID.randomUUID().toString())
                .setLabel("Alice")
                .setLatitude(37.4)
                .setLongitude(-122.122)
                .build();

        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            var compasses = activity.getCompasses();
            LabeledLocationDisplay temp1 = compasses.get(0).createLabeledLocationDisplay();
            LabeledLocationDisplay temp2 = compasses.get(0).createLabeledLocationDisplay();
            LabeledLocationDisplay temp3 = compasses.get(0).createLabeledLocationDisplay();
            compasses.forEach(compass -> {
                temp1.setLabeledLocation(test1);
                compass.getLabeledLocationDisplayMap().put(test1.getPublicCode(), temp1);
                temp2.setLabeledLocation(test2);
                compass.getLabeledLocationDisplayMap().put(test2.getPublicCode(), temp2);
                temp3.setLabeledLocation(test3);
                compass.getLabeledLocationDisplayMap().put(test3.getPublicCode(), temp3);
            });

            LocationService.getInstance().unregisterLocationUpdateListener();
            LocationService.getInstance().setCurrentCoordinate(new Coordinate(37.4220, -122.0840));
            OrientationService.getInstance().unregisterSensorEventUpdateListener();
            OrientationService.getInstance().setCurrentOrientation(0);

            var constraints = compasses.get(3).getDisplayConstraintView();

            var labelLayout1 = (ConstraintLayout.LayoutParams) temp1.getLabelView().getLayoutParams();
            var labelLayout2 = (ConstraintLayout.LayoutParams) temp2.getLabelView().getLayoutParams();

            Assert.assertEquals(constraints.get(temp1).getId(), labelLayout1.topToBottom);
            Assert.assertEquals(constraints.get(temp2).getId(), labelLayout2.topToBottom);
            Assert.assertEquals(temp3.getLabelView().getText(), "Ali");
        });
    }
}
