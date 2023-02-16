package com.cse110.team7.socialcompass;


import static java.lang.Double.parseDouble;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.LatLong;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadProfile();
    }

    public void onGoToCompass(View view) {
        TextView parentCoordinatesView = findViewById(R.id.latLongTextView);
        String parentCoordinates = parentCoordinatesView.getText().toString();

        TextView parentLabelName = findViewById(R.id.parentLabelName);
        String parentLabel = parentLabelName.getText().toString();

        try {
            Intent intent = new Intent(this, CompassActivity.class);

            intent.putExtra("latLong", parentCoordinates);
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

        String latLong = preferences.getString("latLong", "");
        String parentLabel = preferences.getString("parentLabelName", "");

        TextView parentCoordinatesView = findViewById(R.id.latLongTextView);
        TextView parentLabelName = findViewById(R.id.parentLabelName);


        parentCoordinatesView.setText(latLong);
        parentLabelName.setText(parentLabel);
    }

    public void saveProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView parentCoordinatesView = findViewById(R.id.latLongTextView);
        String parentCoordinates = parentCoordinatesView.getText().toString();

        editor.putString("latLong", parentCoordinates);

        TextView parentLabel = findViewById(R.id.parentLabelName);
        editor.putString("parentLabelName", parentLabel.getText().toString());

        editor.apply();
    }
}