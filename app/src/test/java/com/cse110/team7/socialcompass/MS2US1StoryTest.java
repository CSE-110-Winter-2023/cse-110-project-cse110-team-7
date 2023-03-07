package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.backend.LocationAPI;
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
/**
 * Tests Adding Friends to Compass.
 */
public class MS2US1StoryTest {

    FriendAccount testLoc1 = new FriendAccount("Mom", new LatLong(0, 10));


    LocationAPI locAPI;
    private FriendAccountDao friendAccountDao;
    private FriendDatabase friendDatabase;


    public void setupFriends() {
        locAPI = LocationAPI.provide();
        locAPI.putLocation(testLoc1);

        // print private IDs- locations must be manually deleted if program crashes
        System.err.println(testLoc1.getPublicID() + " : " + testLoc1.getPrivateID());
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
        friendDatabase.close();
    }

    @Test
    public void testAddingFriendUsingPremadePubID() {
        //Create Friends:
        setupFriends();


        var scenario = ActivityScenario.launch(AddFriendActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            EditText addUID = activity.findViewById(R.id.promptUID);
            addUID.setText(testLoc1.getPublicID());
            Button addButton = activity.findViewById(R.id.addButton);
            addButton.performClick();
        });

        assertNotNull(friendAccountDao.selectFriend(testLoc1.getId()));
    }
}
