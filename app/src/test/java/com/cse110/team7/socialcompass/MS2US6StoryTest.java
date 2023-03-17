package com.cse110.team7.socialcompass;

import static org.junit.Assert.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

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
public class MS2US6StoryTest   {

   private Coordinate myCoordinates = new Coordinate(32.880209067700804, -117.23403495767106);

   private static final LabeledLocation warrenCollege = new LabeledLocation.Builder()
           .setLabel("Friend1")
           .setLatitude(32.88241546981538)
           .setLongitude(-117.2340876160442)
           .build();

   private static final LabeledLocation sorrentoValley = new LabeledLocation.Builder()
           .setLabel("Friend2")
           .setLatitude(32.89520798145009)
           .setLongitude(-117.18983658532336)
           .build();

   private static final LabeledLocation northClairemont = new LabeledLocation.Builder()
           .setLabel("Friend3")
           .setLatitude(32.8328259)
           .setLongitude(-117.2000351)
           .build();

   private static final LabeledLocation lasVegas = new LabeledLocation.Builder()
           .setLabel("Friend4")
           .setLatitude(36.117211715254264)
           .setLongitude(-115.17245155490667)
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
      var client = new MockServerClient("localhost", 1080);
      setupMockPutServer(warrenCollege, client);
      setupMockPutServer(sorrentoValley, client);
      setupMockPutServer(northClairemont, client);
      setupMockPutServer(lasVegas, client);

      // add three friends
      addFriend(warrenCollege);
      addFriend(sorrentoValley);
      addFriend(northClairemont);
      addFriend(lasVegas);
   }

   @After
   public void destroy() throws ExecutionException, InterruptedException {
      socialCompassDatabase.close();
      mockServer.close();

   }

   @Test
   public void US6StoryTest() {

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
         assertFalse(firstCompass.getHidden());
         assertFalse(secondCompass.getHidden());
         assertTrue(thirdCompass.getHidden());
         assertTrue(fourthCompass.getHidden());
         firstCompass.updateLabeledLocationDisplay();
         int visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
         int visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorrentoValley);
         int visibilityMiddleFar = firstCompass.getVisibilityOfFriend(northClairemont);
         int visibilityFar = firstCompass.getVisibilityOfFriend(lasVegas);

         assertEquals(View.VISIBLE, visibilityNear);
         assertEquals(View.INVISIBLE, visibilityMiddleNear);
         assertEquals(View.INVISIBLE, visibilityMiddleFar);
         assertEquals(View.INVISIBLE, visibilityFar);

         //In depth check of other compasses.
         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorrentoValley);
         assertEquals(View.VISIBLE, visibilityMiddleNear);

         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleFar = secondCompass.getVisibilityOfFriend(northClairemont);
         assertEquals(View.VISIBLE, visibilityMiddleFar);

         thirdCompass.updateLabeledLocationDisplay();
         visibilityFar = thirdCompass.getVisibilityOfFriend(lasVegas);
         assertEquals(View.VISIBLE, visibilityFar);

         // First Zoom Out
         Button zoomOutButton = (Button) activity.findViewById(R.id.zoomOutButton);
         zoomOutButton.performClick();

         //In depth check of first compass.
         assertFalse(firstCompass.getHidden());
         assertFalse(secondCompass.getHidden());
         assertFalse(thirdCompass.getHidden());
         assertTrue(fourthCompass.getHidden());
         firstCompass.updateLabeledLocationDisplay();
         visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
         visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorrentoValley);
         visibilityMiddleFar = firstCompass.getVisibilityOfFriend(northClairemont);
         visibilityFar = firstCompass.getVisibilityOfFriend(lasVegas);

         assertEquals(View.VISIBLE, visibilityNear);
         assertEquals(View.INVISIBLE, visibilityMiddleNear);
         assertEquals(View.INVISIBLE, visibilityMiddleFar);
         assertEquals(View.INVISIBLE, visibilityFar);

         //In depth check of other compasses.
         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorrentoValley);
         assertEquals(View.VISIBLE, visibilityMiddleNear);

         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleFar = secondCompass.getVisibilityOfFriend(northClairemont);
         assertEquals(View.VISIBLE, visibilityMiddleFar);

         thirdCompass.updateLabeledLocationDisplay();
         visibilityFar = thirdCompass.getVisibilityOfFriend(lasVegas);
         assertEquals(View.VISIBLE, visibilityFar);

         // SECOND zoom out
         zoomOutButton.performClick();

         //In depth check of first compass.
         assertFalse(firstCompass.getHidden());
         assertFalse(secondCompass.getHidden());
         assertFalse(thirdCompass.getHidden());
         assertFalse(fourthCompass.getHidden());
         firstCompass.updateLabeledLocationDisplay();
         visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
         visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorrentoValley);
         visibilityMiddleFar = firstCompass.getVisibilityOfFriend(northClairemont);
         visibilityFar = firstCompass.getVisibilityOfFriend(lasVegas);

         assertEquals(View.VISIBLE, visibilityNear);
         assertEquals(View.INVISIBLE, visibilityMiddleNear);
         assertEquals(View.INVISIBLE, visibilityMiddleFar);
         assertEquals(View.INVISIBLE, visibilityFar);

         //In depth check of other compasses.
         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorrentoValley);
         assertEquals(View.VISIBLE, visibilityMiddleNear);

         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleFar = secondCompass.getVisibilityOfFriend(northClairemont);
         assertEquals(View.VISIBLE, visibilityMiddleFar);

         thirdCompass.updateLabeledLocationDisplay();
         visibilityFar = thirdCompass.getVisibilityOfFriend(lasVegas);
         assertEquals(View.VISIBLE, visibilityFar);

         // THIRD zoom out
         zoomOutButton.performClick();

         //In depth check of first compass.
         assertFalse(firstCompass.getHidden());
         assertFalse(secondCompass.getHidden());
         assertFalse(thirdCompass.getHidden());
         assertFalse(fourthCompass.getHidden());
         firstCompass.updateLabeledLocationDisplay();
         visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
         visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorrentoValley);
         visibilityMiddleFar = firstCompass.getVisibilityOfFriend(northClairemont);
         visibilityFar = firstCompass.getVisibilityOfFriend(lasVegas);

         assertEquals(View.VISIBLE, visibilityNear);
         assertEquals(View.INVISIBLE, visibilityMiddleNear);
         assertEquals(View.INVISIBLE, visibilityMiddleFar);
         assertEquals(View.INVISIBLE, visibilityFar);

         //In depth check of other compasses.
         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorrentoValley);
         assertEquals(View.VISIBLE, visibilityMiddleNear);

         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleFar = secondCompass.getVisibilityOfFriend(northClairemont);
         assertEquals(View.VISIBLE, visibilityMiddleFar);

         thirdCompass.updateLabeledLocationDisplay();
         visibilityFar = thirdCompass.getVisibilityOfFriend(lasVegas);
         assertEquals(View.VISIBLE, visibilityFar);

         // Zoom In
         Button zoomInButton = (Button) activity.findViewById(R.id.zoomInButton);
         zoomInButton.performClick();
         zoomInButton.performClick();

         //In depth check of first compass.
         assertFalse(firstCompass.getHidden());
         assertFalse(secondCompass.getHidden());
         assertFalse(thirdCompass.getHidden());
         assertTrue(fourthCompass.getHidden());
         firstCompass.updateLabeledLocationDisplay();
         visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
         visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorrentoValley);
         visibilityMiddleFar = firstCompass.getVisibilityOfFriend(northClairemont);
         visibilityFar = firstCompass.getVisibilityOfFriend(lasVegas);

         assertEquals(View.VISIBLE, visibilityNear);
         assertEquals(View.INVISIBLE, visibilityMiddleNear);
         assertEquals(View.INVISIBLE, visibilityMiddleFar);
         assertEquals(View.INVISIBLE, visibilityFar);

         //In depth check of other compasses.
         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorrentoValley);
         assertEquals(View.VISIBLE, visibilityMiddleNear);

         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleFar = secondCompass.getVisibilityOfFriend(northClairemont);
         assertEquals(View.VISIBLE, visibilityMiddleFar);

         thirdCompass.updateLabeledLocationDisplay();
         visibilityFar = thirdCompass.getVisibilityOfFriend(lasVegas);
         assertEquals(View.VISIBLE, visibilityFar);

         // Second Zoom In
         zoomInButton.performClick();
         //In depth check of first compass.
         assertFalse(firstCompass.getHidden());
         assertFalse(secondCompass.getHidden());
         assertTrue(thirdCompass.getHidden());
         assertTrue(fourthCompass.getHidden());
         firstCompass.updateLabeledLocationDisplay();
         visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
         visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorrentoValley);
         visibilityMiddleFar = firstCompass.getVisibilityOfFriend(northClairemont);
         visibilityFar = firstCompass.getVisibilityOfFriend(lasVegas);

         assertEquals(View.VISIBLE, visibilityNear);
         assertEquals(View.INVISIBLE, visibilityMiddleNear);
         assertEquals(View.INVISIBLE, visibilityMiddleFar);
         assertEquals(View.INVISIBLE, visibilityFar);

         //In depth check of other compasses.
         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorrentoValley);
         assertEquals(View.VISIBLE, visibilityMiddleNear);

         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleFar = secondCompass.getVisibilityOfFriend(northClairemont);
         assertEquals(View.VISIBLE, visibilityMiddleFar);

         thirdCompass.updateLabeledLocationDisplay();
         visibilityFar = thirdCompass.getVisibilityOfFriend(lasVegas);
         assertEquals(View.VISIBLE, visibilityFar);

         // Third Zoom In
         zoomInButton.performClick();

         //In depth check of first compass.
         assertFalse(firstCompass.getHidden());
         assertTrue(secondCompass.getHidden());
         assertTrue(thirdCompass.getHidden());
         assertTrue(fourthCompass.getHidden());
         firstCompass.updateLabeledLocationDisplay();
         visibilityNear = firstCompass.getVisibilityOfFriend(warrenCollege);
         visibilityMiddleNear = firstCompass.getVisibilityOfFriend(sorrentoValley);
         visibilityMiddleFar = firstCompass.getVisibilityOfFriend(northClairemont);
         visibilityFar = firstCompass.getVisibilityOfFriend(lasVegas);

         assertEquals(View.VISIBLE, visibilityNear);
         assertEquals(View.INVISIBLE, visibilityMiddleNear);
         assertEquals(View.INVISIBLE, visibilityMiddleFar);
         assertEquals(View.INVISIBLE, visibilityFar);

         //In depth check of other compasses.
         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleNear = secondCompass.getVisibilityOfFriend(sorrentoValley);
         assertEquals(View.VISIBLE, visibilityMiddleNear);

         secondCompass.updateLabeledLocationDisplay();
         visibilityMiddleFar = secondCompass.getVisibilityOfFriend(northClairemont);
         assertEquals(View.VISIBLE, visibilityMiddleFar);

         thirdCompass.updateLabeledLocationDisplay();
         visibilityFar = thirdCompass.getVisibilityOfFriend(lasVegas);
         assertEquals(View.VISIBLE, visibilityFar);

         zoomOutButton.performClick();
         activity.recreate();
         assertFalse(firstCompass.getHidden());
         assertFalse(secondCompass.getHidden());
         assertTrue(thirdCompass.getHidden());
         assertTrue(fourthCompass.getHidden());

      });
   }


   // do you see the zoom in and out buttons?

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


