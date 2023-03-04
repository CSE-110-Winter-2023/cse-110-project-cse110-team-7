package com.cse110.team7.socialcompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.ui.inputDisplayAdapter;
import com.cse110.team7.socialcompass.ui.inputDislayViewModel;


/*
 * First page of our application; we should probably move all of this over to another activity.
 */
import com.cse110.team7.socialcompass.utils.ShowAlert;


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

        viewModel.getFriendItems().observe(this, adapter::setFriendList);

        //If no data is already saved, then adds three friends to the database.
        viewModel.getFriendItems().observe(this, friends -> {
            if (friends.size() == 0) {
                viewModel.addFriend(new FriendAccount("Parents", null));
                viewModel.addFriend(new FriendAccount("Friends", null));
                viewModel.addFriend(new FriendAccount("My Home", null));
            }
        });

        //Sets up the recycler view, so that each empty/stored label gets displayed on the UI, in the
        //format given by label_input_format.xml
        recyclerView = findViewById(R.id.friendInputItems);
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
        try {
            orientation = Float.parseFloat(mockOrientationStr);
            if(orientation < 0 || orientation > 359) {
                ShowAlert.alert(this, "Please enter a number between 0-359");
                return;
            }
        } catch (NumberFormatException ignored) {
            orientation = -1;
        }
        Intent intent = new Intent(this, CompassActivity.class);
        intent.putExtra("orientation", orientation);

        for(FriendAccount i : adapter.friendAccountList) {
            if (i.getLocation() != null) {
                startActivity(intent);
                return;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}