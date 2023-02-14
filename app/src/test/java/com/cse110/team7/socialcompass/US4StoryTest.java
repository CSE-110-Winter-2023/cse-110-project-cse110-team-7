package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.widget.ImageView;

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
public class US4StoryTest {

   @Test
   public void testNorthLabelExist() {
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
   public void testLocationChange() {
      var scenario = ActivityScenario.launch(CompassActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.STARTED);

      scenario.onActivity(activity -> {
         // stop receiving update from system services, as we want to manually set them
         LocationService.getInstance().unregisterLocationUpdateListener();
         OrientationService.getInstance().unregisterSensorEventListener();

         LatLong fakeLocation = new LatLong(32.88364984363482, -117.23495948855353);
         LatLong fakeParentLocation = new LatLong(32.881189630410475, -117.23758934970839);

         float parentBearing = AngleCalculator.calculateAngle(fakeLocation, fakeParentLocation);

         activity.compass.getAllElements().get(0).setLocation(fakeParentLocation);

         OrientationService.getInstance().setAzimuth(0);
         LocationService.getInstance().setUserLocation(fakeLocation);

         var layoutParams = (ConstraintLayout.LayoutParams) activity.compass.getAllElements().get(0).getDotView().getLayoutParams();

         assertTrue(Double.compare(layoutParams.circleAngle, parentBearing) == 0);
      });
   }
   @Test
   public void testOrientationChange() {
      var scenario = ActivityScenario.launch(CompassActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.STARTED);

      scenario.onActivity(activity -> {
         // stop receiving update from system services, as we want to manually set them
         LocationService.getInstance().unregisterLocationUpdateListener();
         OrientationService.getInstance().unregisterSensorEventListener();

         float fakeOrientation = 90;
         LatLong fakeLocation = new LatLong(32.88364984363482, -117.23495948855353);
         LatLong fakeParentLocation = new LatLong(32.881189630410475, -117.23758934970839);

         float parentBearing = AngleCalculator.calculateAngle(fakeLocation, fakeParentLocation);

         activity.compass.getAllElements().get(0).setLocation(fakeParentLocation);

         LocationService.getInstance().setUserLocation(fakeLocation);
         OrientationService.getInstance().setAzimuth(fakeOrientation);

         var layoutParams = (ConstraintLayout.LayoutParams) activity.compass.getAllElements().get(0).getDotView().getLayoutParams();

         assertTrue(Double.compare(layoutParams.circleAngle, parentBearing - fakeOrientation) == 0);
      });
   }
}
