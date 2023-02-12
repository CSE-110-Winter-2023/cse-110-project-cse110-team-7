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

    LocationManager locationManager;
    SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        ImageView northLabelImageView = (ImageView) findViewById(R.id.labelNorth);
        Compass compass = new Compass(northLabelImageView);

        compass.insertHouse(new House("Parents", new LatLong(32.8835982026854, -117.23493663196449)));

        compass.getAllHouses().forEach(house -> initHouseDisplay(house));

        LocationService.getInstance().setLocationManager(locationManager);
        LocationService.getInstance().registerLocationUpdateListener(this);

        OrientationService.getInstance().setSensorManager(sensorManager);
        OrientationService.getInstance().registerSensorEventListener();

        LocationService.getInstance().getUserLocation().observe(this, (currentLocation) -> {
            compass.getAllHouses().forEach(house -> updateHouse(currentLocation, house));
        });

        OrientationService.getInstance().getAzimuth().observe(this, (currentAzimuth) -> {
            compass.updateRotation(compass.getNorthLabel(), -currentAzimuth);
            compass.getAllHouses().forEach(houseView -> {

            });
        });

    }

    public void updateHouse(LatLong currentLocation, House house) {
        ElementDisplay houseView = house.getHouseDisplay();
        ConstraintLayout.LayoutParams dotViewParameters = (ConstraintLayout.LayoutParams) houseView.getDotView().getLayoutParams();
        dotViewParameters.circleAngle = AngleCalculator.calculateAngle(currentLocation, house.getLocation());

        houseView.getDotView().setLayoutParams(dotViewParameters);
    }

    public void initHouseDisplay(House house) {
        ImageView dotView = new ImageView(this);

        dotView.setId(View.generateViewId());
        dotView.setImageResource(R.drawable.blue_circle);

        TextView labelView = new TextView(this);

        labelView.setId(View.generateViewId());
        labelView.setText(house.getLabelName());
        labelView.setTextSize(20); //Change size of text here.
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setTextColor(Color.BLACK);

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
        labelParameters.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        // labelParameters.startToStart = dotView.getId();
        // labelParameters.endToEnd = dotView.getId();

        house.setDisplay(new ElementDisplay(labelView, dotView));

    }
}