package com.cse110.team7.socialcompass;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.Compass;
import com.cse110.team7.socialcompass.ui.LabelInformation;

import java.util.ArrayList;
import java.util.List;

public class CompassActivity extends AppCompatActivity {

    private Compass compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);


        //Sets North Label to always be the correct radius, regardless of size of the screen.
        ImageView northLabel = findViewById(R.id.labelNorth);
        ((ConstraintLayout.LayoutParams) northLabel.getLayoutParams()).circleRadius =
                Math.min((getScreenWidth() * 6) / 14 - 10, (getScreenHeight()  * 5) / 15 - 10);

        //Instantiates the Compass and adds the northLabel to it.
        compass = new Compass(northLabel);


        //These three lines open up the Room database, giving us access to the values stored from
        //the main activity, with HouseDao being an instance of the HouseDatabase class.
        Context context = getApplication().getApplicationContext();
        FriendDatabase houseDao = FriendDatabase.getInstance(context);
        final FriendAccountDao db = houseDao.getFriendDao();

        // Accessing data from input screen
        Intent intent = getIntent();
        float mockOrientation = intent.getFloatExtra("orientation", -1);

        //We read all of the houses stored in the database, which gives us a live observer variable
        //And from there, if the location is not null (e.g. was not inputted), it adds it as a label
        //to the compass.
        db.selectFriends().observe(this, houses -> {
            for(FriendAccount i : houses){
                if(i.getLocation() != null){
                    compass.add(initHouseDisplay(i));
                }
            }
        });


        //Maybe refactor this into its own method.

        // Default location from API is Google HQ in San Francisco
        // You can change the location and the orientation of the emulator in "Extended Controls" (3 dots)
        // To change orientation go to "Virtual sensors -> Device Pose"
        // Set "X-Rot" to about -60 and slide "Z-Rot" to change the orientation

        LocationService.getInstance().setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));

        // Shijun will fix this, to make it work better.
        while (true) {
            try {
                LocationService.getInstance().registerLocationUpdateListener(this);
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
                try {
                    wait(1000);
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }

        //Maybe Refactor this into it's own method.
        // Sets up sensors to read values.
        OrientationService.getInstance().setSensorManager((SensorManager) getSystemService(Context.SENSOR_SERVICE));

        //Updates compass based on changing location values.
        LocationService.getInstance().getUserLocation().observe(this, (currentLocation) -> {
            compass.updateBearingForAll(currentLocation);
            compass.updateRotationForAll();
        });

        OrientationService.getInstance().getAzimuth().observe(this, (currentAzimuth) -> {
            compass.updateAzimuth(currentAzimuth);
            compass.updateRotationForAll();
        });

        // override with mock orientation
        if (mockOrientation >=  0) {
            OrientationService.getInstance().setAzimuth(mockOrientation);
        } else {
            OrientationService.getInstance().registerSensorEventListener();
        }

    }

    //Creates a label for each house contained in the database.
    public LabelInformation initHouseDisplay(FriendAccount friendAccount) {
        ImageView dotView = new ImageView(this);

        dotView.setId(View.generateViewId());
        dotView.setImageResource(R.drawable.blue_circle);

        TextView labelView = new TextView(this);

        labelView.setId(View.generateViewId());
        labelView.setText(friendAccount.getName());
        labelView.setTextSize(20); //Change size of text here.
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setTextColor(Color.WHITE);
        labelView.setShadowLayer(6, 1, 1, Color.BLACK);

        // Pulls Primary Constraint from activity_compass.xml
        ConstraintLayout layout = findViewById(R.id.compassActivityParentConstraints);
        layout.addView(dotView, -1);
        layout.addView(labelView, -1);

        ConstraintLayout.LayoutParams dotViewParameters = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();

        dotViewParameters.circleConstraint = R.id.CompassCenter;
        //Sets all Houses to always be the correct radius, regardless of size of the screen.
        dotViewParameters.circleRadius = Math.min((getScreenWidth() * 5) / 14, (getScreenHeight()  * 4) / 15);

        dotViewParameters.circleAngle = 0; //shouldn't this be the actual initial angle
        dotViewParameters.width = 60;
        dotViewParameters.height = 60;

        dotView.setLayoutParams(dotViewParameters);

        ConstraintLayout.LayoutParams labelParameters = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();

        labelParameters.topToBottom = dotView.getId();
        labelParameters.startToStart = dotView.getId();
        labelParameters.endToEnd = dotView.getId();
        labelParameters.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        labelParameters.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        return new LabelInformation(friendAccount, dotView, labelView);
    }

    public Compass getCompass() {
        return compass;
    }


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    // Updates the house database when the app is exited.
    @Override
    protected void onStop() {
        super.onStop();

        FriendDatabase houseDao = FriendDatabase.getInstance(getApplicationContext());
        final FriendAccountDao db = houseDao.getFriendDao();

        List<FriendAccount> friendAccounts = new ArrayList<>();

        // Uses all house labels excluding north label
        for(LabelInformation label : compass.getElements()) {
            if(!label.equals(compass.getNorthElementDisplay())) {
                friendAccounts.add(new FriendAccount(label.getHouse().getName(), label.getHouse().getLocation()));
            }
        }

        for(FriendAccount friendAccount : friendAccounts) {
            db.updateFriend(friendAccount);
        }
    }

    public void onGoToInput(View view) {
        LocationService.getInstance().unregisterLocationUpdateListener();
        OrientationService.getInstance().unregisterSensorEventListener();

        finish();
        super.onBackPressed();
    }
}