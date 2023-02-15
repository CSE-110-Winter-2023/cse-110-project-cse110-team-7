package com.cse110.team7.socialcompass;

import static java.lang.Double.parseDouble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.LatLong;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        loadProfile();
    }

    public void onGoToCompass(View view) {
        TextView parentCoordinatesView = findViewById(R.id.latLongTextView);
        String parentCoordinates = parentCoordinatesView.getText().toString();

        TextView parentLabelName = findViewById(R.id.parentLabelName);
        String parentLabel = parentLabelName.getText().toString();

        LatLong parentLatLong = stringToLatLong(parentCoordinates);

        try {
            float latitude = (float)parentLatLong.getLatitude();
            float longitude = (float)parentLatLong.getLongitude();

            Intent intent = new Intent(this, CompassActivity.class);

            intent.putExtra("lat", latitude);
            intent.putExtra("long", longitude);
            intent.putExtra("parentLabelName", parentLabel);

            startActivity(intent);
        } catch (NumberFormatException ignored) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProfile();
    }

    public void loadProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        String lat = preferences.getString("lat", "");
        String longitude = (preferences.getString("long", ""));
        String parentLabel = preferences.getString("parentLabelName", "");

        TextView parentCoordinatesView = findViewById(R.id.latLongTextView);
        TextView parentLabelName = findViewById(R.id.parentLabelName);


        parentCoordinatesView.setText(lat + ", " + longitude);
        parentLabelName.setText(parentLabel);
    }

    public void saveProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView parentCoordinatesView = findViewById(R.id.latLongTextView);
        String parentCoordinates = parentCoordinatesView.getText().toString();

        LatLong toSave = stringToLatLong(parentCoordinates);

        editor.putFloat("lat", (float)(toSave.getLatitude()));
        editor.putFloat("long", (float)(toSave.getLongitude()));

        TextView parentLabel = findViewById(R.id.parentLabelName);

        editor.putString("parentLabelName", parentLabel.getText().toString());

        editor.apply();
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