package com.cse110.team7.socialcompass;


import android.content.Context;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.database.LabeledLocationDao;
import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.server.ServerAPI;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.concurrent.ExecutionException;

@RunWith(RobolectricTestRunner.class)
public class MS2US2StoryTest {
    private static final LabeledLocation testLocation1 = new LabeledLocation.Builder()
            .setLabel("Mom")
            .setLatitude(0)
            .setLongitude(1)
            .build();


    private static final LabeledLocation testLocation2 = new LabeledLocation.Builder()
            .setLabel("Dad")
            .setLatitude(1)
            .setLongitude(1)
            .build();


    private static final LabeledLocation testLocation3 = new LabeledLocation.Builder()
            .setLabel("Friend")
            .setLatitude(45)
            .setLongitude(0)
            .build();

    private static final LabeledLocation testLocation4 = new LabeledLocation.Builder()
            .setLabel("Friend")
            .setLatitude(40)
            .setLongitude(100)
            .build();

    private SocialCompassDatabase socialCompassDatabase;
    private LabeledLocationDao labeledLocationDao;

    @Before
    public void init() throws ExecutionException, InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        SocialCompassDatabase.injectTestDatabase(
                Room.inMemoryDatabaseBuilder(context, SocialCompassDatabase.class)
                        .allowMainThreadQueries()
                        .build()
        );

        socialCompassDatabase = SocialCompassDatabase.getInstance(context);
        labeledLocationDao = socialCompassDatabase.getLabeledLocationDao();

        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation1).get();
        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation2).get();
        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation3).get();
        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation4).get();
    }

    @After
    public void destroy() throws ExecutionException, InterruptedException {
        socialCompassDatabase.close();

        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation1).get();
        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation2).get();
        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation3).get();
        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation4).get();
    }

    @Test
    public void US2StoryTest() {
        addFriend(testLocation1);

        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation1.getPublicCode()));
        Assert.assertNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation2.getPublicCode()));
        Assert.assertNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation3.getPublicCode()));
        Assert.assertNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation4.getPublicCode()));

        addFriend(testLocation2);

        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation1.getPublicCode()));
        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation2.getPublicCode()));
        Assert.assertNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation3.getPublicCode()));
        Assert.assertNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation4.getPublicCode()));

        addFriend(testLocation3);

        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation1.getPublicCode()));
        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation2.getPublicCode()));
        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation3.getPublicCode()));
        Assert.assertNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation4.getPublicCode()));

        addFriend(testLocation4);

        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation1.getPublicCode()));
        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation2.getPublicCode()));
        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation3.getPublicCode()));
        Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation4.getPublicCode()));
    }

    public void addFriend(LabeledLocation labeledLocation) {
        var scenario = ActivityScenario.launch(AddFriendActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            activity.getFriendUIDEditText().setText(labeledLocation.getPublicCode());
            activity.getFriendUIDEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getAddFriendButton().performClick();
        });
    }
}
