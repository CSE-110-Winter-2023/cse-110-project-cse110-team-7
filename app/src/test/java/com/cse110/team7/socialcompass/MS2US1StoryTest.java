package com.cse110.team7.socialcompass;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

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
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.concurrent.ExecutionException;


@RunWith(RobolectricTestRunner.class)
public class MS2US1StoryTest {
    private static final LabeledLocation testLocation = new LabeledLocation.Builder()
            .setLabel("Mom")
            .setLatitude(0)
            .setLongitude(10)
            .setPublicCode("1234")
            .build();

    private SocialCompassDatabase socialCompassDatabase;
    private LabeledLocationDao labeledLocationDao;

    private ClientAndServer mockServer;

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

        mockServer = startClientAndServer(1080);

        ServerAPI.getInstance().changeEndpoint("http://localhost:1080");
        new MockServerClient("localhost", 1080)
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/location/1234")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{\n" +
                                        "  \"public_code\": \"1234\",\n" +
                                        "  \"label\": \"Mom\",\n" +
                                        "  \"latitude\": 0,\n" +
                                        "  \"longitude\": 10,\n" +
                                        "  \"created_at\": \"2023-02-18T12:00:00Z\",\n" +
                                        "  \"updated_at\": \"2023-02-18T18:30:00Z\"\n" +
                                        "}")
                );
    }

    @After
    public void destroy() {
        socialCompassDatabase.close();
        mockServer.stop();
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