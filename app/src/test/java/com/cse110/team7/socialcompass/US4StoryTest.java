package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.location.Location;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.LabelInformation;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@RunWith(RobolectricTestRunner.class)
public class US4StoryTest {
   @Test
   public void testDataPersistence() {
      var scenario = ActivityScenario.launch(CompassActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.STARTED);

      scenario.onActivity(activity -> {
         LocationService.getInstance().unregisterLocationUpdateListener();
         OrientationService.getInstance().unregisterSensorEventListener();

         LatLong fakeFriendLocation = new LatLong(50, -120);
         LatLong fakeParentLocation = new LatLong(32, -117);

         House fakeParentHouse = new House("fakeParentHouse", fakeParentLocation);
         activity.getCompass().add(activity.initHouseDisplay(fakeParentHouse));

         activity.getCompass().getElements().get(0).getHouse().setLocation(fakeFriendLocation);
         activity.getCompass().getElements().get(1).getHouse().setLocation(fakeParentLocation);
         activity.recreate();

         LatLong newFriendLocation = activity.getCompass().getElements().get(0).getHouse().getLocation();
         LatLong newParentLocation = activity.getCompass().getElements().get(1).getHouse().getLocation();
         assertEquals(Double.compare(newFriendLocation.getLatitude(), 50), 0);
         assertEquals(Double.compare(newFriendLocation.getLongitude(), -120), 0);
         assertEquals(Double.compare(newParentLocation.getLatitude(), 32), 0);
         assertEquals(Double.compare(newParentLocation.getLongitude(), -117), 0);
      });
   }
}
