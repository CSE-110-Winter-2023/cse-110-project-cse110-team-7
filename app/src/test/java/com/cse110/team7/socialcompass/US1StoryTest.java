package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertNotNull;

import android.widget.ImageView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class US1StoryTest   {
    @Test
    public void US1Atest() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            ImageView compass = (ImageView) activity.findViewById(R.id.compassImage);
            ImageView north = (ImageView) activity.findViewById(R.id.labelNorth);
            assertNotNull(compass);
            assertNotNull(north);
        });
    }
}
