package com.cse110.team7.socialcompass;

import android.content.Context;
import android.content.DialogInterface;
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
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.concurrent.ExecutionException;

/**
 * Tests User Story 1: Adding Friends to the Compass.
 */
@RunWith(RobolectricTestRunner.class)
public class MS2US1StoryTest {
    private static final LabeledLocation testLocation = new LabeledLocation.Builder()
            .setLabel("Mom")
            .setLatitude(0)
            .setLongitude(10)
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

        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation).get();
    }

    @After
    public void destroy() throws ExecutionException, InterruptedException {
        socialCompassDatabase.close();

        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation).get();
    }

    @Test
    public void US1StoryTest() {
        var scenario = ActivityScenario.launch(AddFriendActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            activity.getFriendUIDEditText().setText("");
            activity.getFriendUIDEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getAddFriendButton().performClick();

            var latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
            Assert.assertNotNull(latestAlertDialog);

            Assert.assertNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation.getPublicCode()));

            latestAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

            activity.getFriendUIDEditText().setText(testLocation.getPublicCode());
            activity.getFriendUIDEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getAddFriendButton().performClick();

            Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation.getPublicCode()));
        });
    }
}
