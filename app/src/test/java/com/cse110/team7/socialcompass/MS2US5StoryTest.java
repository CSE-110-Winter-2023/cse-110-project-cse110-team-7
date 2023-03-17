/**
 * Tests US5 from Milestone 2: Gauge Friend's Distance
 */
package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.lifecycle.Lifecycle;
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
import com.cse110.team7.socialcompass.ui.Compass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RunWith(RobolectricTestRunner.class)
public class MS2US5StoryTest {

    // Coordinates of UCSD
    private Coordinate myCoordinates = new Coordinate(32.880209067700804, -117.23403495767106);

    private LabeledLocation warrenCollege = new LabeledLocation.Builder()
            .setLabel("Warren College")
            .setLatitude(32.88241546981538)
            .setLongitude(-117.2340876160442)
            .build();

    private LabeledLocation sorentoValley = new LabeledLocation.Builder()
            .setLabel("Sorento Valley")
            .setLatitude(32.89520798145009)
            .setLongitude(-117.18983658532336)
            .build();

    private LabeledLocation lasVegas = new LabeledLocation.Builder()
            .setLabel("Las Vegas")
            .setLatitude(36.117211715254264)
            .setLongitude(-115.17245155490667)
            .build();


    private LabeledLocation northPole = new LabeledLocation.Builder()
            .setLabel("North Pole")
            .setLatitude(90)
            .setLongitude(-135)
            .build();

    private ClientAndServer mockServer;

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

        mockServer = startClientAndServer(1080);

        ServerAPI.getInstance().changeEndpoint("http://localhost:1080");
        var client = new MockServerClient("localhost", 1080);
        setupMockPutServer(warrenCollege, client);
        setupMockPutServer(sorentoValley, client);
        setupMockPutServer(lasVegas, client);
        setupMockPutServer(northPole, client);

        addFriend(warrenCollege);
        addFriend(sorentoValley);
        addFriend(lasVegas);
        addFriend(northPole);
    }

    @After
    public void destroy() throws ExecutionException, InterruptedException {
        socialCompassDatabase.close();

        mockServer.close();
    }

    //Assumes all 4 circles are showing.
    @Test
    public void initialFriendPositions() {
        // Startup From Main Activity
        var scenarioOne = ActivityScenario.launch(MainActivity.class);
        scenarioOne.moveToState(Lifecycle.State.CREATED);
        scenarioOne.moveToState(Lifecycle.State.STARTED);

        scenarioOne.onActivity(activity -> {
            activity.getNameEditText().setText("UCSD Location");
            activity.getNameEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);
            activity.getOkButton().performClick();
        });


        var scenarioTwo = ActivityScenario.launch(CompassActivity.class);
        scenarioTwo.onActivity(activity -> {
            //Setting Location to UCSD:
            LocationService.getInstance().unregisterLocationUpdateListener();
            OrientationService.getInstance().unregisterSensorEventUpdateListener();

            LocationService.getInstance().setCurrentCoordinate(myCoordinates);
            OrientationService.getInstance().setCurrentOrientation(0); //Facing North



            // Get all compass.
            List<Compass> allCompasses = activity.getCompasses();
            Compass firstCompass = null;
            Compass secondCompass = null;
            Compass thirdCompass = null;
            Compass fourthCompass = null;

            for(Compass compasses : allCompasses){
                if(compasses.circleType == compasses.FIRST_CIRCLE) {
                    firstCompass = compasses;
                }
                if(compasses.circleType == compasses.SECOND_CIRCLE) {
                    secondCompass = compasses;
                }
                if(compasses.circleType == compasses.THIRD_CIRCLE) {
                    thirdCompass = compasses;
                }
                if(compasses.circleType == compasses.FOURTH_CIRCLE) {
                    fourthCompass = compasses;
                }
            }



            //Should have found compasses.
            assertNotNull(firstCompass);

            int endIndx = firstCompass.getCompassTag().indexOf("[");
            assertEquals("[" + 0.0 + ", " + 1.0 + ")",
                    firstCompass.getCompassTag().substring(endIndx));

            assertNotNull(secondCompass);
            assertNotNull(thirdCompass);
            assertNotNull(fourthCompass);



            //In depth check of first compass.
            firstCompass.updateLabeledLocationDisplay();
            int visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
            int visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorentoValley);
            int visibilityMiddleFar = firstCompass.getVisibilityOfFriend(lasVegas);
            int visibilityFar = firstCompass.getVisibilityOfFriend(northPole);

            assertEquals(View.VISIBLE, visibilityNear);
            assertEquals(View.INVISIBLE, visibilityMiddleNear);
            assertEquals(View.INVISIBLE, visibilityMiddleFar);
            assertEquals(View.INVISIBLE, visibilityFar);


            //Brief check of remaining ones:
            secondCompass.updateLabeledLocationDisplay();
            visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorentoValley);
            assertEquals(View.VISIBLE, visibilityMiddleNear);

            thirdCompass.updateLabeledLocationDisplay();
            visibilityMiddleFar = thirdCompass.getVisibilityOfFriend(lasVegas);
            assertEquals(View.VISIBLE, visibilityMiddleFar);

            fourthCompass.updateLabeledLocationDisplay();
            visibilityFar = fourthCompass.getVisibilityOfFriend(northPole);
            assertEquals(View.VISIBLE, visibilityFar);








            // Now we move our position to be at same position as las vegas.
            Coordinate lasVegasCoordinates =
                    new Coordinate(36.117211715254264,-115.17245155490667);
            LocationService.getInstance().setCurrentCoordinate(lasVegasCoordinates);
            OrientationService.getInstance().setCurrentOrientation(0); //Facing North


            //Another in-depth check of first compass.
            visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
            visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorentoValley);
            visibilityMiddleFar = firstCompass.getVisibilityOfFriend(lasVegas);
            visibilityFar = firstCompass.getVisibilityOfFriend(northPole);

            assertEquals(View.INVISIBLE, visibilityNear);
            assertEquals(View.INVISIBLE, visibilityMiddleNear);
            assertEquals(View.VISIBLE, visibilityMiddleFar);
            assertEquals(View.INVISIBLE, visibilityFar);
        });
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
