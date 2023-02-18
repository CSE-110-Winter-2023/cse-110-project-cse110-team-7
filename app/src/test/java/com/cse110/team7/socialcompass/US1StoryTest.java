package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class US1StoryTest   {
    @Test
    public void US1testCase1() {
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

    @Test
    public void US1testCase2() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            ImageView image = (ImageView) activity.findViewById(R.id.labelNorth);
            Rect rt = image.getDrawable().getBounds();

            int drawLeft = rt.left;
            int drawTop = rt.top;
            int drawRight = rt.right;
            int drawBottom = rt.bottom;

            assertEquals(drawLeft, 0);
            assertEquals(drawTop, 0);
            assertEquals(drawRight, 2000);
            assertEquals(drawBottom, 2000);
        });
    }
}
