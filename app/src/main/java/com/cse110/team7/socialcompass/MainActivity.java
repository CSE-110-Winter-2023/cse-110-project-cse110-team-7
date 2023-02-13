package com.cse110.team7.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadProfile();
    }

    public void onGoToCompass(View view) {
        TextView latView = findViewById(R.id.latTextView);
        String latStr = latView.getText().toString();
        TextView longView = findViewById(R.id.longTextView);
        String longStr = longView.getText().toString();

        Intent intent = new Intent(this, CompassActivity.class);

        intent.putExtra("lat", latStr);
        intent.putExtra("long", longStr);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveProfile();
    }

    public void loadProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String lat = preferences.getString("lat", "");
        String longitude = preferences.getString("long", "");
        TextView latView = findViewById(R.id.latTextView);
        TextView longView = findViewById(R.id.longTextView);
        latView.setText(lat);
        longView.setText(longitude);

    }

    public void saveProfile() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        TextView latView = findViewById(R.id.latTextView);
        TextView longView = findViewById(R.id.longTextView);
        editor.putString("name", latView.getText().toString());
        editor.putString("status", longView.getText().toString());

        editor.apply();
    }
}