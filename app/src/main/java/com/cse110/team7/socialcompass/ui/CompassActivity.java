package com.cse110.team7.socialcompass.ui;

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
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

import java.util.ArrayList;

public class CompassActivity extends AppCompatActivity {

    public float azimuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        azimuth = 0.0f;

        ArrayList<House> savedHouses = new ArrayList<>();
        ArrayList<HouseDisplayView> houseViews = new ArrayList<>();

        ImageView northLabel = (ImageView) findViewById(R.id.labelNorth);

        House north = new House("", new LatLong( 90, 0));
        HouseDisplayView northView = new HouseDisplayView(north, northLabel, null);
        houseViews.add(northView);

        // Default location from API is Google HQ in San Francisco
        // You can change the location and the orientation of the emulator in "Extended Controls" (3 dots)
        // To change orientation go to "Virtual sensors -> Device Pose"
        // Set "X-Rot" to about -60 and slide "Z-Rot" to change the orientation
        savedHouses.add(new House("San Diego", new LatLong(32.712486975797596, -117.16466382307459)));
        savedHouses.add(new House("New York", new LatLong(40.73935160994699, -74.02956535148193)));
        savedHouses.add(new House("London", new LatLong(51.562348041090466, -0.1271620157993551)));
        savedHouses.add(new House("Shanghai", new LatLong(31.325989153258618, 121.42715900305875)));
        savedHouses.add(new House("Sydney", new LatLong(-33.26071143320893, 151.32305353994278)));

        savedHouses.forEach(house -> houseViews.add(initHouse(house)));

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
            houseViews.forEach(houseView -> houseView.updateBearing(AngleCalculator.calculateAngle(currentLocation, houseView.getHouse().getLocation())));
            updateRotationForAll(houseViews);
        });

        OrientationService.getInstance().getAzimuth().observe(this, (currentAzimuth) -> {
            azimuth = currentAzimuth;
            updateRotationForAll(houseViews);
        });

    }

    public void updateRotationForAll(ArrayList<HouseDisplayView> houseViews) {
        houseViews.forEach(this::updateRotation);
    }

    public void updateRotation(HouseDisplayView houseVIew) {
        ConstraintLayout.LayoutParams imageBasicLayout = (ConstraintLayout.LayoutParams) houseVIew.getDotView().getLayoutParams();
        imageBasicLayout.circleAngle = houseVIew.getBearing() - azimuth;
        houseVIew.getDotView().setLayoutParams(imageBasicLayout);
    }

    public HouseDisplayView initHouse(House house) {
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
        dotViewParameters.circleAngle = 0;
        dotViewParameters.width = 60;
        dotViewParameters.height = 60;

        dotView.setLayoutParams(dotViewParameters);

        ConstraintLayout.LayoutParams labelParameters = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();

        labelParameters.topToBottom = dotView.getId();
        labelParameters.startToStart = dotView.getId();
        labelParameters.endToEnd = dotView.getId();
        labelParameters.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        labelParameters.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        labelView.setLayoutParams(labelParameters);

        return new HouseDisplayView(house, dotView, labelView);
    }
}