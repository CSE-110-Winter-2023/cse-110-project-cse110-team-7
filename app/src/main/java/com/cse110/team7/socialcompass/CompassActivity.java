package com.cse110.team7.socialcompass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.R;
import com.cse110.team7.socialcompass.models.Compass;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.ElementDisplay;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import java.util.ArrayList;

public class CompassActivity extends AppCompatActivity {

    public float azimuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        ImageView northLabel = (ImageView) findViewById(R.id.labelNorth);
        Compass compass = new Compass(northLabel);
        azimuth = 0.0f;
        // Default location from API is Google HQ in San Francisco
        // You can change the location and the orientation of the emulator in "Extended Controls" (3 dots)
        // To change orientation go to "Virtual sensors -> Device Pose"
        // Set "X-Rot" to about -60 and slide "Z-Rot" to change the orientation

        compass.insert(new ElementDisplay("San Diego", new LatLong(32.712486975797596, -117.16466382307459)));
        compass.insert(new ElementDisplay("New York", new LatLong(40.73935160994699, -74.02956535148193)));
        compass.insert(new ElementDisplay("London", new LatLong(51.562348041090466, -0.1271620157993551)));
        compass.insert(new ElementDisplay("Shanghai", new LatLong(31.325989153258618, 121.42715900305875)));
        compass.insert(new ElementDisplay("Sydney", new LatLong(-33.26071143320893, 151.32305353994278)));

        compass.getAllElements().forEach(house -> initHouseDisplay(house));

        LocationService.getInstance().setLocationManager((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        while (true) {
            try {
                LocationService.getInstance().registerLocationUpdateListener(this);
                break;
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        OrientationService.getInstance().setSensorManager((SensorManager) getSystemService(Context.SENSOR_SERVICE));
        OrientationService.getInstance().registerSensorEventListener();

        LocationService.getInstance().getUserLocation().observe(this, (currentLocation) -> {
            // System.out.println(currentLocation.toString());
            compass.getAllElements().forEach(house -> house.updateBearing(AngleCalculator.calculateAngle(currentLocation, house.getLocation())));
            updateRotation(compass.getNorthLabel());
            updateRotationForAll(compass.getAllElements());
        });

        OrientationService.getInstance().getAzimuth().observe(this, (currentAzimuth) -> {
            azimuth = currentAzimuth;
            updateRotation(compass.getNorthLabel());
            updateRotationForAll(compass.getAllElements());
        });


    }

    public void updateRotationForAll(ArrayList<ElementDisplay> houses) {
        houses.forEach(this::updateRotation);
    }

    public void updateRotation(ElementDisplay house) {
        ConstraintLayout.LayoutParams imageBasicLayout = (ConstraintLayout.LayoutParams) house.getDotView().getLayoutParams();
        imageBasicLayout.circleAngle = house.getBearing() - azimuth;
        house.getDotView().setLayoutParams(imageBasicLayout);
    }

    public void initHouseDisplay(ElementDisplay house) {
        ImageView dotView = new ImageView(this);

        dotView.setId(View.generateViewId());
        dotView.setImageResource(R.drawable.blue_circle);

        TextView labelView = new TextView(this);

        labelView.setId(View.generateViewId());
        labelView.setText(house.getLabelName());
        labelView.setTextSize(20); //Change size of text here.
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setTextColor(Color.WHITE);
        labelView.setShadowLayer(6, 1, 1, Color.BLACK);

        // Pulls Primary Constraint from activity_compass.xml
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.compassActivityParentConstraints);
        layout.addView(dotView, -1);
        layout.addView(labelView, -1);

        ConstraintLayout.LayoutParams dotViewParameters = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();

        dotViewParameters.circleConstraint = R.id.CompassCenter;
        dotViewParameters.circleRadius = 380;
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

        house.setDotView(dotView);
        house.setLabelView(labelView);
    }
}