package com.cse110.team7.socialcompass;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import android.content.Context;
import android.view.inputmethod.EditorInfo;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.database.LabeledLocationDao;
import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.server.LabeledLocationRepository;
import com.cse110.team7.socialcompass.server.ServerAPI;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowLooper;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class MS2US4StoryTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();
    private static final LabeledLocation testLocation1 = new LabeledLocation.Builder()
            .setLabel("Mom")
            .setLatitude(40)
            .setLongitude(50)
            .build();

    private static final LabeledLocation testLocation2 = new LabeledLocation.Builder().build();


    private SocialCompassDatabase socialCompassDatabase;
    private LabeledLocationDao labeledLocationDao;
    private LabeledLocationRepository labeledLocationRepository;

    private ClientAndServer mockServer;
    private MockServerClient client;

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
        labeledLocationRepository = new LabeledLocationRepository(labeledLocationDao);

        mockServer = startClientAndServer(1080);

        ServerAPI.getInstance().changeEndpoint("http://localhost:1080");
        client = new MockServerClient("localhost", 1080);
        setupMockPutServer(testLocation1, client);
    }

    @After
    public void destroy() throws ExecutionException, InterruptedException {
        socialCompassDatabase.close();
        mockServer.close();
    }

    @Test
    public void US4StoryTest1() throws ExecutionException, InterruptedException {
        var scenario = ActivityScenario.launch(AddFriendActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            activity.getFriendUIDEditText().setText(testLocation1.getPublicCode());
            activity.getFriendUIDEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getAddFriendButton().performClick();
        });

        Assert.assertTrue(labeledLocationDao.selectLabeledLocationWithoutLiveData(
                testLocation1.getPublicCode()).getCoordinate().equals(testLocation1.getCoordinate()));
    }

    @Test
    public void US4StoryTest2() {
        var scenario1 = ActivityScenario.launch(AddFriendActivity.class);
        scenario1.moveToState(Lifecycle.State.CREATED);
        scenario1.moveToState(Lifecycle.State.STARTED);

        scenario1.onActivity(activity -> {
            activity.getFriendUIDEditText().setText(testLocation1.getPublicCode());
            activity.getFriendUIDEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getAddFriendButton().performClick();
        });
    }

    public void setupMockPutServer(LabeledLocation testLocation, MockServerClient client) {

        client.when(
                        request()
                                .withMethod("GET")
                                .withPath("/location/" + testLocation.getPublicCode())
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{\n" +
                                        "  \"public_code\": \""+ testLocation.getPublicCode() + "\",\n" +
                                        "  \"label\": \""+ testLocation.getLabel() + "\",\n" +
                                        "  \"latitude\": "+ testLocation.getLatitude() + ",\n" +
                                        "  \"longitude\": "+ testLocation.getLongitude() + ",\n" +
                                        "  \"created_at\": \"2023-02-18T12:00:00Z\",\n" +
                                        "  \"updated_at\": \"2023-02-18T18:30:00Z\"\n" +
                                        "}")
                );
    }
}
