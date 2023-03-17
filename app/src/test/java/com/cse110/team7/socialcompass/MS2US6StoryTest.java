package com.cse110.team7.socialcompass;

import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.cse110.team7.socialcompass.ui.Compass;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.stream.Collectors;

@RunWith(RobolectricTestRunner.class)
public class MS2US6StoryTest {

    @Test
    public void testUS6() {
        var scenarioOne = ActivityScenario.launch(CompassActivity.class);
        scenarioOne.moveToState(Lifecycle.State.CREATED);
        scenarioOne.moveToState(Lifecycle.State.STARTED);

        scenarioOne.onActivity(activity -> {
            var compasses = activity.getCompasses();

            Assert.assertEquals(2, activity.getZoomLevel());
            Assert.assertEquals(2, compasses.stream().filter(Compass::isHidden).count());

            activity.getZoomInButton().performClick();

            Assert.assertEquals(2, activity.getZoomLevel());
            Assert.assertEquals(2, compasses.stream().filter(Compass::isHidden).count());

            activity.getZoomOutButton().performClick();

            Assert.assertEquals(3, activity.getZoomLevel());
            Assert.assertEquals(1, compasses.stream().filter(Compass::isHidden).count());

            activity.getZoomOutButton().performClick();

            Assert.assertEquals(4, activity.getZoomLevel());
            Assert.assertEquals(0, compasses.stream().filter(Compass::isHidden).count());

            activity.getZoomOutButton().performClick();

            Assert.assertEquals(4, activity.getZoomLevel());
            Assert.assertEquals(0, compasses.stream().filter(Compass::isHidden).count());
        });
    }
}
