package com.cse110.team7.socialcompass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.ui.InputDisplayAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InputDisplayAdapter adapter = new InputDisplayAdapter();
        adapter.setHasStableIds(true);

        //Start temporary:
        House tempAdd = new House("Parents", new LatLong(31, 31));
        House tempAdd2 = new House("Friends", new LatLong(31, 31));
        House tempAdd3 = new House("My Home", new LatLong(31, 31));

        //Type type = new TypeToken<List<House>>(){}.getType();
        List<House> houseList = new ArrayList<House>();
        houseList.add(tempAdd);
        houseList.add(tempAdd2);
        houseList.add(tempAdd3);
        adapter.setHouseList(houseList);
        //End temp

        recyclerView = findViewById(R.id.houseInputItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

//        loadProfile();
    }

    public void onGoToCompass(View view) {
//        TextView latView = findViewById(R.id.latTextView);
//        String latStr = latView.getText().toString();
//        TextView longView = findViewById(R.id.longTextView);
//        String longStr = longView.getText().toString();

        try {
//            float latitude = Float.parseFloat(latStr);
//            float longitude = Float.parseFloat(longStr);

            Intent intent = new Intent(this, CompassActivity.class);

//            intent.putExtra("lat", latitude);
//            intent.putExtra("long", longitude);

            startActivity(intent);
        } catch (NumberFormatException ignored) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        saveProfile();
    }

//    public void loadProfile() {
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//
//        String lat = preferences.getString("lat", "");
//        String longitude = preferences.getString("long", "");
//        TextView latView = findViewById(R.id.latTextView);
//        TextView longView = findViewById(R.id.longTextView);
//
//        latView.setText(lat);
//        longView.setText(longitude);
//    }

//    public void saveProfile() {
//        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = preferences.edit();
//
//        TextView latView = findViewById(R.id.latTextView);
//        TextView longView = findViewById(R.id.longTextView);
//        editor.putString("lat", latView.getText().toString());
//        editor.putString("long", longView.getText().toString());
//
//        editor.apply();
//    }
}