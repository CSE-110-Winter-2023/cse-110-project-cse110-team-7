package com.cse110.team7.socialcompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cse110.team7.socialcompass.utils.ShowAlert;
import org.w3c.dom.Text;
import com.cse110.team7.socialcompass.backend.HouseDatabase;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.ui.inputDisplayAdapter;
import com.cse110.team7.socialcompass.ui.inputDislayViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * First page of our application; we should probably move all of this over to another activity.
 */
public class MainActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    inputDisplayAdapter adapter;
    inputDislayViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tracks interactions between the UI and the database, allowing us to update values as they
        //get changed.
        viewModel = new ViewModelProvider(this).get(inputDislayViewModel.class);

        //Creates new adapter, which does the actual updating of values.
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

        //Sets up the recycler view, so that each empty/stored label gets displayed on the UI, in the
        //format given by label_input_format.xml
        recyclerView = findViewById(R.id.houseInputItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * On button click, only goes to CompassActivity if at least one location has been inputted.
     */
    public void onGoToCompass(View view) {

        TextView mockOrientation = findViewById(R.id.mockOrientationView);
        String mockOrientationStr = mockOrientation.getText().toString();

        float orientation;
        Intent intent = new Intent(this, CompassActivity.class);
        try {


            orientation = Float.parseFloat(mockOrientationStr);
            if(orientation < 0 || orientation > 359) {
                ShowAlert.alert(this, "Please enter a number between 0-359");
                return;
            }


            intent.putExtra("orientation", orientation);
        } catch (NumberFormatException ignored) {
            orientation = -1;
        }
        // check that at least one house in inputted
        for(House i : adapter.houseList){
            if(i.getLocation() != null){
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}