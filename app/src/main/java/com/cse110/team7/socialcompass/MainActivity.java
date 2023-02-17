package com.cse110.team7.socialcompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.backend.HouseDatabase;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.ui.inputDisplayAdapter;
import com.cse110.team7.socialcompass.ui.inputDislayViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    inputDisplayAdapter adapter;
    inputDislayViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(inputDislayViewModel.class);

        //Creates new adapter
        adapter = new inputDisplayAdapter();
        adapter.setHasStableIds(true);

        //Binds methods to adapter
        adapter.setCoordinatesChanged(viewModel::updateCoordinateText);
        adapter.setParentLabelChanged(viewModel::updateLabelText);

        viewModel.getHouseItems().observe(this, adapter::setHouseList);

        //If no data is already saved, then adds three empty houses to the database.
        viewModel.getHouseItems().observe(this, houses -> {
            if (houses.size() == 0) {
                viewModel.addHouse(new House("Parents", null));
                viewModel.addHouse(new House("Friends", null));
                viewModel.addHouse(new House("My Home", null));
            }
        });

        recyclerView = findViewById(R.id.houseInputItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onGoToCompass(View view) {

        try {

            Intent intent = new Intent(this, CompassActivity.class);
//            intent.putExtra("House List", adapter); //Not working.

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