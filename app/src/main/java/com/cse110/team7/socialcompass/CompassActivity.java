package com.cse110.team7.socialcompass;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.server.LabeledLocationRepository;
import com.cse110.team7.socialcompass.services.LocationService;
import com.cse110.team7.socialcompass.services.OrientationService;
import com.cse110.team7.socialcompass.ui.Compass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CompassActivity extends AppCompatActivity {
    private static final int MIN_ZOOM_LEVEL = 2;
    private static final int MAX_ZOOM_LEVEL = 4;
    private ConstraintLayout compassConstraintLayout;
    private FloatingActionButton addFriendFloatingActionButton;
    private Button zoomInButton;
    private Button zoomOutButton;
    private int zoomLevel;
    private String userPublicCode;
    private LabeledLocationRepository repo;
    private MutableLiveData<List<LiveData<LabeledLocation>>> syncedLabeledLocations;

    List<Compass> allCompasses;
    //private Compass compass;
    private LabeledLocation userLabeledLocation;
    private boolean localUpdateRequired;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        compassConstraintLayout = findViewById(R.id.compassConstraintLayout);
        addFriendFloatingActionButton = findViewById(R.id.addFriendFloatingActionButton);

        zoomInButton = findViewById(R.id.zoomInButton);
        zoomOutButton = findViewById(R.id.zoomOutButton);

        var database = SocialCompassDatabase.getInstance(this);
        var labeledLocationDao = database.getLabeledLocationDao();
        repo = new LabeledLocationRepository(labeledLocationDao);

        var preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userPublicCode = preferences.getString("userPublicCode", null);

        userLabeledLocation = repo.selectLocalLabeledLocationWithoutLiveData(userPublicCode);
        var localLabeledLocations = repo.selectLocalLabeledLocations();

        localUpdateRequired = true;
        syncedLabeledLocations = new MutableLiveData<>();

        localLabeledLocations.observe(this, labeledLocations -> {
            // we only observe once for local locations (the first time data is retrieved)
            // if we continue to observe, there might be recursive update issues
            if (!localUpdateRequired) return;

            Log.i(
                    CompassActivity.class.getName(),
                    "local labeled location update received, current local labeled locations are " + labeledLocations.stream()
                            .map(LabeledLocation::getLabel)
                            .collect(Collectors.joining(", "))
            );

            syncedLabeledLocations.postValue(
                    labeledLocations.stream()
                            .filter(labeledLocation -> !labeledLocation.getPublicCode().equals(userPublicCode))
                            .map(labeledLocation -> repo.syncedSelectLabeledLocation(labeledLocation.getPublicCode()))
                            .collect(Collectors.toList())
            );

            localUpdateRequired = false;
        });

        // Create Compasses:
        allCompasses = createFourCompasses();

        zoomLevel = preferences.getInt("zoomLevel", MIN_ZOOM_LEVEL);
        zoomInButton.setClickable(zoomLevel != MIN_ZOOM_LEVEL);
        zoomOutButton.setClickable(zoomLevel != MAX_ZOOM_LEVEL);
        updateCompassByZoomLevel();

        syncedLabeledLocations.observe(this, labeledLocations -> {
            Log.i(CompassActivity.class.getName(), "synced labeled location update received");

            for(Compass compass : allCompasses) {
                labeledLocations.forEach(compass::displayLabeledLocation);
            }
        });

        compassConstraintLayout.post(() -> {
            int radius = compassConstraintLayout.getWidth() / 2;
            Log.i(CompassActivity.class.getName(), "radius update received, current radius is " + radius);

            allCompasses.forEach(compass -> compass.setRadius(radius));
        });

        LocationService.getInstance().setLocationManager((LocationManager) getSystemService(LOCATION_SERVICE));
        OrientationService.getInstance().setSensorManager((SensorManager) getSystemService(SENSOR_SERVICE));
        OrientationService.getInstance().setWindowManager((WindowManager) getSystemService(WINDOW_SERVICE));

        askForPermissionAndRegisterLocationUpdateListener();

        OrientationService.getInstance().registerSensorEventUpdateListener();
        Log.i(CompassActivity.class.getName(), "orientation update listener registered");

        LocationService.getInstance().getCurrentCoordinate().observe(this, currentCoordinate -> {
            if (userLabeledLocation != null) {
                userLabeledLocation.setCoordinate(currentCoordinate);
                repo.syncedUpsert(userLabeledLocation);
            }

            allCompasses.forEach(compass -> compass.updateBearingForAll(currentCoordinate));
        });

        OrientationService.getInstance().getCurrentOrientation().observe(this, currentOrientation -> {
            allCompasses.forEach(compass -> compass.updateOrientationForAll(currentOrientation));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        localUpdateRequired = true;
    }

    public List<Compass> createFourCompasses() {
        List<Compass> allCompasses = new ArrayList<>();

        Compass compass1 = new Compass(this, compassConstraintLayout,
                0, 1,
                Compass.FIRST_CIRCLE, screenSize());  //Inner circle: should always be showing.
        Compass compass2 = new Compass(this, compassConstraintLayout,
                1, 10,
                Compass.SECOND_CIRCLE, screenSize());
        Compass compass3 = new Compass(this, compassConstraintLayout,
                10, 500,
                Compass.THIRD_CIRCLE, screenSize());
        Compass compass4 = new Compass(this, compassConstraintLayout,
                500, 65536_000,
                Compass.FOURTH_CIRCLE, screenSize());

        allCompasses.add(compass1);
        allCompasses.add(compass2);
        allCompasses.add(compass3);
        allCompasses.add(compass4);

        return allCompasses;
    }

    public int screenSize(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = (int) (displayMetrics.heightPixels * .9);
        int width = (int) (displayMetrics.widthPixels * .9);

        return Math.min(height, width);
    }

    public void askForPermissionAndRegisterLocationUpdateListener() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    LocationService.getInstance().registerLocationUpdateListener();
                    Log.i(CompassActivity.class.getName(), "location update listener registered");
                }
            }).launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            LocationService.getInstance().registerLocationUpdateListener();
            Log.i(CompassActivity.class.getName(), "location update listener registered");
        }
    }

    public void onAddFriendFloatingActionButtonClicked(View view) {
        Log.i(CompassActivity.class.getName(), "add friend floating action button clicked");

        Intent intent = new Intent(this, AddFriendActivity.class);
        intent.putExtra("userUID", userLabeledLocation.getPublicCode());

        startActivity(intent);
    }

    public void updateCompassByZoomLevel() {
        Log.i(CompassActivity.class.getName(), "compass at index " + (zoomLevel - 1) + " is outer compass");

        for (int compassIndex = 0; compassIndex < allCompasses.size(); compassIndex++) {
            var compass = allCompasses.get(compassIndex);

            if (compassIndex < zoomLevel) {
                compass.setHidden(false);
                compass.setScale((compassIndex + 1.0) / zoomLevel);
            } else {
                compass.setHidden(true);
            }

            compass.setLastCompass(compassIndex == zoomLevel - 1);
        }
    }

    public void saveZoomLevel() {
        var preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        var editor = preferences.edit();

        editor.putInt("zoomLevel", zoomLevel);
        editor.apply();
    }

    public void onZoomInButtonClicked(View view) {
        Log.i(CompassActivity.class.getName(), "zoom in button clicked");

        zoomLevel -= 1;
        zoomOutButton.setClickable(true);

        if (zoomLevel == MIN_ZOOM_LEVEL) {
            zoomInButton.setClickable(false);
        }

        updateCompassByZoomLevel();
        saveZoomLevel();
    }

    public void onZoomOutClicked(View view) {
        Log.i(CompassActivity.class.getName(), "zoom out button clicked");

        zoomLevel += 1;
        zoomInButton.setClickable(true);

        if (zoomLevel == MAX_ZOOM_LEVEL) {
            zoomOutButton.setClickable(false);
        }

        updateCompassByZoomLevel();
        saveZoomLevel();
    }

    @VisibleForTesting
    public ConstraintLayout getCompassConstraintLayout() {
        return compassConstraintLayout;
    }

    @VisibleForTesting
    public FloatingActionButton getAddFriendFloatingActionButton() {
        return addFriendFloatingActionButton;
    }

    @VisibleForTesting
    public List<Compass> getCompasses() {
        return allCompasses;
    }

    @VisibleForTesting
    public LabeledLocation getUserLabeledLocation() {
        return userLabeledLocation;
    }

    @VisibleForTesting
    public Button getZoomInButton() {
        return zoomInButton;
    }

    @VisibleForTesting
    public Button getZoomOutButton() {
        return zoomOutButton;
    }
}