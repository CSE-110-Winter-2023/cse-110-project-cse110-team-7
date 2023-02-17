package com.cse110.team7.socialcompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.backend.HouseDao;
import com.cse110.team7.socialcompass.backend.HouseDatabase;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.ui.InputDisplayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    InputDisplayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates new adapter
        adapter = new InputDisplayAdapter();
        adapter.setHasStableIds(true); //May be unecessary.

        //Loads saved values
        HouseDao houseDao = HouseDatabase.getInstance(this).getHouseDao();
        LiveData<List<House>> houseListLive = houseDao.selectHouses();
        List<House> houseList = houseListLive.getValue(); //Current Values of Houses

        //If no data is already saved, then adds three empty houses to the database.
        if(houseList == null || houseList.size() != 0){
            House parentsHome = new House("Parents", null);
            House friendsHome = new House("Friends", null);
            House myHome = new House("My Home", null);

            houseList = new ArrayList<House>();
            houseList.add(parentsHome);
            houseList.add(friendsHome);
            houseList.add(myHome);
        }

        adapter.setHouseList(houseList); //Displays Houses Currently In Database

        recyclerView = findViewById(R.id.houseInputItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Over here we need to enable saving stored values:
    }

    public void onGoToCompass(View view) {

        try {

            Intent intent = new Intent(this, CompassActivity.class);
            intent.putExtra("House List", adapter); //Not working.

            startActivity(intent);
        } catch (NumberFormatException ignored) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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