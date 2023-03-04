package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.ImageView;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

@RunWith(RobolectricTestRunner.class)
public class US3StoryTest {
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
   public void testNorthLabelExist() {
      var scenario = ActivityScenario.launch(CompassActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.STARTED);

      scenario.onActivity(activity -> {
         ImageView compass = activity.findViewById(R.id.compassImage);
         ImageView north = activity.findViewById(R.id.labelNorth);

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
         LocationService.getInstance().unregisterLocationUpdateListener();
         OrientationService.getInstance().unregisterSensorEventListener();


         LatLong fakeLocation = new LatLong(32.88364984363482, -117.23495948855353);
         LatLong fakeParentLocation = new LatLong(32.881189630410475, -117.23758934970839);

         float parentBearing = AngleCalculator.calculateAngle(fakeLocation, fakeParentLocation);

         FriendAccount fakeParentFriendAccount = new FriendAccount("fakeParentHouse", fakeParentLocation);
         activity.getCompass().add(activity.initHouseDisplay(fakeParentFriendAccount));

         activity.getCompass().getElements().get(1).getHouse().setLocation(fakeParentLocation);

         OrientationService.getInstance().setAzimuth(0);
         LocationService.getInstance().setUserLocation(fakeLocation);

         var layoutParams = (ConstraintLayout.LayoutParams) activity.getCompass().getElements().get(1).getDotView().getLayoutParams();

         assertEquals(Double.compare(layoutParams.circleAngle, parentBearing), 0);
      });
   }

   @Test
   public void testOrientationChange() {
      var scenario = ActivityScenario.launch(CompassActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.STARTED);

      scenario.onActivity(activity -> {
         LocationService.getInstance().unregisterLocationUpdateListener();
         OrientationService.getInstance().unregisterSensorEventListener();

         float fakeOrientation = 90;
         LatLong fakeLocation = new LatLong(32.88364984363482, -117.23495948855353);
         LatLong fakeParentLocation = new LatLong(32.881189630410475, -117.23758934970839);

         float parentBearing = AngleCalculator.calculateAngle(fakeLocation, fakeParentLocation);

         FriendAccount fakeParentFriendAccount2 = new FriendAccount("fakeParentHouse", fakeParentLocation);
         activity.getCompass().add(activity.initHouseDisplay(fakeParentFriendAccount2));


         activity.getCompass().getElements().get(1).getHouse().setLocation(fakeParentLocation);

         LocationService.getInstance().setUserLocation(fakeLocation);
         OrientationService.getInstance().setAzimuth(fakeOrientation);

         var layoutParams = (ConstraintLayout.LayoutParams) activity.getCompass().getElements().get(1).getDotView().getLayoutParams();

         assertEquals(Double.compare(layoutParams.circleAngle, parentBearing - fakeOrientation), 0);
      });
   }

   @Test
   public void us3StoryTest() {
      ArrayList<String> houseNames = new ArrayList<>(Arrays.asList("a", "b", "c"));
      ArrayList<Double> randomHouseLats = (ArrayList<Double>) (new Random().doubles(3, -90.0, 90.0)).boxed().collect(Collectors.toList());
      ArrayList<Double> randomHouseLongs = (ArrayList<Double>) (new Random().doubles(3, -180.0, 180.0)).boxed().collect(Collectors.toList());
      ArrayList<Double> randomLats = (ArrayList<Double>) (new Random().doubles(10, -90.0, 90.0)).boxed().collect(Collectors.toList());
      ArrayList<Double> randomLongs = (ArrayList<Double>) (new Random().doubles(10, -180.0, 180.0)).boxed().collect(Collectors.toList());
      ArrayList<Double> randomOrientations = (ArrayList<Double>) (new Random().doubles(10, 0, 2 * Math.PI)).boxed().collect(Collectors.toList());

      ArrayList<FriendAccount> savedFriendAccounts = new ArrayList<>();
      for (int i = 0; i < houseNames.size(); i++) {
         savedFriendAccounts.add(new FriendAccount(houseNames.get(i), new LatLong(randomHouseLats.get(i), randomHouseLongs.get(i))));
      }

      ArrayList<LatLong> randomLocations = new ArrayList<>();
      for (int i = 0; i < randomLats.size(); i++) {
         randomLocations.add(new LatLong(randomLats.get(i), randomLongs.get(i)));
      }

      var scenario = ActivityScenario.launch(CompassActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.STARTED);

      scenario.onActivity(activity -> {

         LocationService.getInstance().unregisterLocationUpdateListener();
         OrientationService.getInstance().unregisterSensorEventListener();

         savedFriendAccounts.forEach(house -> activity.getCompass().add(activity.initHouseDisplay(house)));

         for (int i = 0; i < randomLocations.size(); i++) {
            LatLong randomLocation = randomLocations.get(i);
            float randomOrientation = randomOrientations.get(i).floatValue();
            LocationService.getInstance().setUserLocation(randomLocation);
            OrientationService.getInstance().setAzimuth(randomOrientation);
            activity.getCompass().getElements().forEach(houseLabel -> {
               float bearing = AngleCalculator.calculateAngle(randomLocation, houseLabel.getHouse());
               var layoutParams = (ConstraintLayout.LayoutParams) houseLabel.getDotView().getLayoutParams();
               assertEquals(Double.compare(layoutParams.circleAngle, bearing - randomOrientation), 0);
            });
         }
      });

   }

}

