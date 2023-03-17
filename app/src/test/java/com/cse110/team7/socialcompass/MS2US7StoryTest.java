package com.cse110.team7.socialcompass;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.cse110.team7.socialcompass.services.LocationService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MS2US7StoryTest {

    @Test
    public void US7StoryTest() {
        var scenario = ActivityScenario.launch(CompassActivity.class);

        LocationService locationService = LocationService.getInstance();

        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {

            assertNull(activity.getGpsIndicator().getColorFilter());
            assertEquals("", activity.getLastSignalTime().getText());

            locationService.getFormattedLastSignalTime().setValue(null);
            activity.getLastSignalTime().setText(locationService.getFormattedLastSignalTime().getValue());

            assertEquals(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP), activity.getGpsIndicator().getColorFilter());
            assertEquals("", activity.getLastSignalTime().getText());

            locationService.getFormattedLastSignalTime().setValue("1m");
            activity.getLastSignalTime().setText(locationService.getFormattedLastSignalTime().getValue());

            assertEquals(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP), activity.getGpsIndicator().getColorFilter());
            assertEquals("1m", activity.getLastSignalTime().getText());
            assertEquals(Color.RED, activity.getLastSignalTime().getCurrentTextColor());
            assertEquals(View.VISIBLE, activity.getLastSignalTime().getVisibility());

            locationService.getFormattedLastSignalTime().setValue("2h");
            activity.getLastSignalTime().setText(locationService.getFormattedLastSignalTime().getValue());

            assertEquals(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP), activity.getGpsIndicator().getColorFilter());
            assertEquals("2h", activity.getLastSignalTime().getText());
            assertEquals(Color.RED, activity.getLastSignalTime().getCurrentTextColor());
            assertEquals(View.VISIBLE, activity.getLastSignalTime().getVisibility());

            locationService.getFormattedLastSignalTime().setValue("");
            activity.getLastSignalTime().setText(locationService.getFormattedLastSignalTime().getValue());

            assertEquals(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP), activity.getGpsIndicator().getColorFilter());
            assertEquals("", activity.getLastSignalTime().getText());
        });
    }
}



