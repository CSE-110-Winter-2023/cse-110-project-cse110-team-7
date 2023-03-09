package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.backend.LocationAPI;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MS2US2StoryTest {

    FriendAccount testLoc1 = new FriendAccount("Mom", new LatLong(0, 1));
    FriendAccount testLoc2 = new FriendAccount("Dad", new LatLong(1, 1));
    FriendAccount testLoc3 = new FriendAccount("Friend", new LatLong(100, 0));
    FriendAccount testLoc4 = new FriendAccount("Best Friend", new LatLong(100, 100));


    LocationAPI locAPI;

    private FriendAccountDao friendAccountDao;
    private FriendDatabase friendDatabase;

    @Before
    /* populate server */
    public void setup() {
        locAPI = LocationAPI.provide();
        locAPI.putLocation(testLoc1);
        // print private IDs- locations must be manually deleted if program crashes
        System.err.println(testLoc1.getPublicID() + " : " + testLoc1.getPrivateID());
        locAPI.putLocation(testLoc2);
        System.err.println(testLoc2.getPublicID() + " : " + testLoc2.getPrivateID());
        locAPI.putLocation(testLoc3);
        System.err.println(testLoc3.getPublicID() + " : " + testLoc3.getPrivateID());
        locAPI.putLocation(testLoc4);
        System.err.println(testLoc4.getPublicID() + " : " + testLoc4.getPrivateID());

    }

    @After
    public void cleanServer() {
        locAPI.deleteFriend(testLoc1);
        locAPI.deleteFriend(testLoc2);
        locAPI.deleteFriend(testLoc3);
        locAPI.deleteFriend(testLoc4);

    }

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
            locAPI.deleteFriend(testLoc1);
            locAPI.deleteFriend(testLoc2);
            locAPI.deleteFriend(testLoc3);
            friendDatabase.close();
        }

        @Test
        public void US2StoryTest() {

            addUser(testLoc1);
            addUser(testLoc2);
            assertNotNull(friendAccountDao.selectFriend(testLoc1.getId()));
            assertNotNull(friendAccountDao.selectFriend(testLoc2.getId()));
            assertNull(friendAccountDao.selectFriend(testLoc3.getId()));
            assertNull(friendAccountDao.selectFriend(testLoc4.getId()));

            addUser(testLoc3);
            assertNotNull(friendAccountDao.selectFriend(testLoc1.getId()));
            assertNotNull(friendAccountDao.selectFriend(testLoc2.getId()));
            assertNotNull(friendAccountDao.selectFriend(testLoc3.getId()));
            assertNull(friendAccountDao.selectFriend(testLoc4.getId()));

            addUser(testLoc4);
            assertNotNull(friendAccountDao.selectFriend(testLoc1.getId()));
            assertNotNull(friendAccountDao.selectFriend(testLoc2.getId()));
            assertNotNull(friendAccountDao.selectFriend(testLoc3.getId()));
            assertNotNull(friendAccountDao.selectFriend(testLoc4.getId()));

        }

        public void addUser(FriendAccount friend) {
            var scenario = ActivityScenario.launch(AddFriendActivity.class);
            scenario.moveToState(Lifecycle.State.CREATED);
            scenario.moveToState(Lifecycle.State.STARTED);

            scenario.onActivity(activity -> {
                EditText addUID = activity.findViewById(R.id.promptUID);
                addUID.setText(friend.getPublicID());
                Button addButton = activity.findViewById(R.id.addButton);
                addButton.performClick();
            });
        }
}
