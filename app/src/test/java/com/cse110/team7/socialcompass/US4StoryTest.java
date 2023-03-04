
package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class US4StoryTest {
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
   public void testDataPersistence() {
      var scenario = ActivityScenario.launch(CompassActivity.class);
      scenario.moveToState(Lifecycle.State.CREATED);
      scenario.moveToState(Lifecycle.State.STARTED);

      scenario.onActivity(activity -> {
         LocationService.getInstance().unregisterLocationUpdateListener();
         OrientationService.getInstance().unregisterSensorEventListener();

         LatLong fakeFriendLocation = new LatLong(50, -120);
         LatLong fakeParentLocation = new LatLong(32, -117);

         FriendAccount fakeParentFriendAccount = new FriendAccount("fakeParentHouse", fakeParentLocation);
         activity.getCompass().add(activity.initHouseDisplay(fakeParentFriendAccount));

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

      FriendAccount friendAccount1 = new FriendAccount("Parents", new LatLong(100, -20));
      FriendAccount friendAccount2 = new FriendAccount("Friend", new LatLong(150, -70));
      FriendAccount friendAccount3 = new FriendAccount("Me", new LatLong(300, 150));

      long id1 = friendAccountDao.insertFriend(friendAccount1);
      long id2 = friendAccountDao.insertFriend(friendAccount2);
      long id3 = friendAccountDao.insertFriend(friendAccount3);

      FriendAccount insertedFriendAccount1 = friendAccountDao.selectFriend(id1);
      FriendAccount insertedFriendAccount2 = friendAccountDao.selectFriend(id2);
      FriendAccount insertedFriendAccount3 = friendAccountDao.selectFriend(id3);

      scenario.moveToState(Lifecycle.State.CREATED);

      scenario.moveToState(Lifecycle.State.RESUMED);

      scenario.onActivity(activity -> {
         assertEquals("Parents", insertedFriendAccount1.getName());
         assertEquals("Friend", insertedFriendAccount2.getName());
         assertEquals("Me", insertedFriendAccount3.getName());

         assertEquals(new LatLong(100, -20), insertedFriendAccount1.getLocation());
         assertEquals(new LatLong(150, -70), insertedFriendAccount2.getLocation());
         assertEquals(new LatLong(300, 150), insertedFriendAccount3.getLocation());
      });
   }


}

