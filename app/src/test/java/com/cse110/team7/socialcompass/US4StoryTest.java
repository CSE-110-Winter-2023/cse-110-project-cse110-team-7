
package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.location.Location;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.backend.HouseDao;
import com.cse110.team7.socialcompass.backend.HouseDatabase;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.LabelInformation;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import org.junit.After;
import org.junit.Before;
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
   private HouseDao houseDao;
   private HouseDatabase houseDatabase;

   @Before
   public void createDatabase() {
      Context context = ApplicationProvider.getApplicationContext();

      houseDatabase = Room.inMemoryDatabaseBuilder(context, HouseDatabase.class)
              .allowMainThreadQueries()
              .build();

      HouseDatabase.injectTestDatabase(houseDatabase);

      houseDao = houseDatabase.getHouseDao();
   }

   @After
   public void closeDatabase() {
      houseDatabase.close();
   }

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

   @Test
   public void testStopPersistence(){
      var scenario = ActivityScenario.launch(CompassActivity.class);

      scenario.moveToState(Lifecycle.State.RESUMED);

      House house1 = new House("Parents", new LatLong(100, -20));
      House house2 = new House("Friend", new LatLong(150, -70));
      House house3 = new House("Me", new LatLong(300, 150));

      long id1 = houseDao.insertHouse(house1);
      long id2 = houseDao.insertHouse(house2);
      long id3 = houseDao.insertHouse(house3);

      House insertedHouse1 = houseDao.selectHouse(id1);
      House insertedHouse2 = houseDao.selectHouse(id2);
      House insertedHouse3 = houseDao.selectHouse(id3);

      scenario.moveToState(Lifecycle.State.CREATED);

      scenario.moveToState(Lifecycle.State.RESUMED);

      scenario.onActivity(activity -> {
         assertEquals("Parents", insertedHouse1.getName());
         assertEquals("Friend", insertedHouse2.getName());
         assertEquals("Me", insertedHouse3.getName());

         assertEquals(new LatLong(100, -20), insertedHouse1.getLocation());
         assertEquals(new LatLong(150, -70), insertedHouse2.getLocation());
         assertEquals(new LatLong(300, 150), insertedHouse3.getLocation());
      });
   }


}

