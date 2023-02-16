package com.cse110.team7.socialcompass;

import static java.lang.Double.parseDouble;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.ui.Compass;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.LabelInformation;

import java.util.ArrayList;

public class CompassActivity extends AppCompatActivity {

    private Compass compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        ArrayList<House> savedHouses = new ArrayList<>();

        ImageView northLabel = findViewById(R.id.labelNorth);
        //Sets North Label to always be the correct radius, regardless of size of the screen.
        ((ConstraintLayout.LayoutParams)northLabel.getLayoutParams()).circleRadius =
                Math.min((getScreenWidth() * 6) / 14 - 10, (getScreenHeight()  * 5) / 15 - 10);
        compass = new Compass(northLabel);

        // Accessing data from input screen
        Intent intent = getIntent();
        String inputLatLong = intent.getStringExtra("latLong");
        String parentLabelName = intent.getStringExtra("parentLabelName");


        savedHouses.add(new House(parentLabelName, stringToLatLong(inputLatLong)));

        savedHouses.forEach(house -> compass.add(initHouseDisplay(house)));

        // Default location from API is Google HQ in San Francisco
        // You can change the location and the orientation of the emulator in "Extended Controls" (3 dots)
        // To change orientation go to "Virtual sensors -> Device Pose"
        // Set "X-Rot" to about -60 and slide "Z-Rot" to change the orientation

        LocationService.getInstance().setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        // this is bad, may need to refactor
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
        OrientationService.getInstance().setSensorManager((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        OrientationService.getInstance().registerSensorEventListener();

        LocationService.getInstance().getUserLocation().observe(this, (currentLocation) -> {
            compass.updateBearingForAll(currentLocation);
            compass.updateRotationForAll();
        });

        OrientationService.getInstance().getAzimuth().observe(this, (currentAzimuth) -> {
            compass.updateAzimuth(currentAzimuth);
            compass.updateRotationForAll();
        });


    }

    public LabelInformation initHouseDisplay(House house) {
        ImageView dotView = new ImageView(this);

        dotView.setId(View.generateViewId());
        dotView.setImageResource(R.drawable.blue_circle);

        TextView labelView = new TextView(this);

        labelView.setId(View.generateViewId());
        labelView.setText(house.getName());
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

        return new LabelInformation(house, dotView, labelView);
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

    //Just temporarily pulled this method from backend (to be merged with this) to make it easier to rework things later
    public static LatLong stringToLatLong(String value) {
        if (value == null) return null;

        String[] latitudeAndLongitude = value.split(",");

        if (latitudeAndLongitude.length != 2) return null;

        double latitude = parseDouble(latitudeAndLongitude[0]);
        double longitude = parseDouble(latitudeAndLongitude[1]);

        return new LatLong(latitude, longitude);
    }
}