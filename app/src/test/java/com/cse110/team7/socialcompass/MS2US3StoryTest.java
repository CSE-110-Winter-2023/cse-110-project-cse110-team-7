package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.backend.LocationAPI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.concurrent.TimeUnit;


@RunWith(RobolectricTestRunner.class)
public class MS2US3StoryTest {


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
    public void US3Test() {
        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            assertEquals("", activity.getNameView().getText().toString());
            assertEquals("N/A", activity.getUidView().getText().toString());

            activity.getOkButton().performClick();

            var latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog() ;

            assertNotNull(latestAlertDialog);

            latestAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

            assertEquals("", activity.getNameView().getText().toString());
            assertEquals("N/A", activity.getUidView().getText().toString());

            activity.getNameView().setText("james");
            activity.getNameView().onEditorAction(EditorInfo.IME_ACTION_DONE);

            assertEquals("james", activity.getNameView().getText().toString());
            assertNotEquals("N/A", activity.getUidView().getText().toString());

            activity.getOkButton().performClick();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

            var uid = preferences.getString("myPublicID", null);
            assertNotNull(uid);
        });
    }
}
