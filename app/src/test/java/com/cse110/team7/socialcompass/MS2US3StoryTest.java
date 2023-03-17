package com.cse110.team7.socialcompass;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.database.LabeledLocationDao;
import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.server.ServerAPI;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.concurrent.ExecutionException;

/**
 * Tests US3: Sending Your Location to the Server.
 */
@RunWith(RobolectricTestRunner.class)
public class MS2US3StoryTest {
    private String uid;
    private SocialCompassDatabase socialCompassDatabase;
    private LabeledLocationDao labeledLocationDao;
    private ClientAndServer mockServer;

    @Before
    public void init() {
        Context context = ApplicationProvider.getApplicationContext();

        SocialCompassDatabase.injectTestDatabase(
                Room.inMemoryDatabaseBuilder(context, SocialCompassDatabase.class)
                        .allowMainThreadQueries()
                        .build()
        );

        socialCompassDatabase = SocialCompassDatabase.getInstance(context);
        labeledLocationDao = socialCompassDatabase.getLabeledLocationDao();

        mockServer = startClientAndServer(1080);
    }

    @After
    public void destroy() {
        socialCompassDatabase.close();
        mockServer.close();
    }

    @Test
    public void US3StoryTest() {
        Context context = ApplicationProvider.getApplicationContext();

        var scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            Assert.assertEquals("", activity.getNameEditText().getText().toString());
            Assert.assertEquals(context.getResources().getString(R.string.user_uid_placeholder), activity.getUidTextView().getText().toString());

            activity.getOkButton().performClick();

            var latestAlertDialog = ShadowAlertDialog.getLatestAlertDialog();
            Assert.assertNotNull(latestAlertDialog);

            latestAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();

            Assert.assertEquals("", activity.getNameEditText().getText().toString());
            Assert.assertEquals(context.getResources().getString(R.string.user_uid_placeholder), activity.getUidTextView().getText().toString());

            activity.getNameEditText().setText("james");
            activity.getNameEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);

            Assert.assertEquals("james", activity.getNameEditText().getText().toString());
            Assert.assertNotEquals(context.getResources().getString(R.string.user_uid_placeholder), activity.getUidTextView().getText().toString());

            activity.getOkButton().performClick();

            var preferences = PreferenceManager.getDefaultSharedPreferences(context);

            uid = preferences.getString("userPublicCode", null);
            Assert.assertNotNull(uid);

            mockUIServer();

            Assert.assertNotNull(labeledLocationDao.selectLabeledLocationWithoutLiveData(uid));
            try {
                Assert.assertNotNull(ServerAPI.getInstance().asyncGetLabeledLocation(uid).get());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    void mockUIServer() {
        ServerAPI.getInstance().changeEndpoint("http://localhost:1080");
        new MockServerClient("localhost", 1080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/location/"+uid)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{\n" +
                                        "  \"public_code\": \""+uid+"\",\n" +
                                        "  \"label\": \"Mom\",\n" +
                                        "  \"latitude\": 0,\n" +
                                        "  \"longitude\": 10,\n" +
                                        "  \"created_at\": \"2023-02-18T12:00:00Z\",\n" +
                                        "  \"updated_at\": \"2023-02-18T18:30:00Z\"\n" +
                                        "}")
                );
    }
}
