package com.cse110.team7.socialcompass;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.backend.LocationAPI;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.ui.InputDisplayAdapter;
import com.cse110.team7.socialcompass.ui.InputDisplayViewModel;


/*
 * First page of our application; we should probably move all of this over to another activity.
 */
import com.cse110.team7.socialcompass.utils.ShowAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;


public class MainActivity extends AppCompatActivity  {
    public RecyclerView recyclerView;
    InputDisplayAdapter adapter;
    InputDisplayViewModel viewModel;

    LocationAPI serverAPI;

    private ScheduledFuture<?> future;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Tracks interactions between the UI and the database, allowing us to update values as they
        //get changed.
        viewModel = new ViewModelProvider(this).get(InputDisplayViewModel.class);

        //Creates new adapter, which does the actual updating of values.
        adapter = new InputDisplayAdapter();
        adapter.setHasStableIds(true);

        //Binds methods to adapter
        adapter.setCoordinatesChanged(viewModel::updateCoordinateText);
        adapter.setParentLabelChanged(viewModel::updateLabelText);

        viewModel.getFriendItems().observe(this, adapter::setFriendList);

        /*
         * TODO:: This will need to be adjusted to work with the database and to sync properly
         * once we add the 'add friend button' and it may need to be moved as necessary.
         */

        serverAPI =  LocationAPI.provide();
        List<String> allFriends = getNeededPublicIDs();
        List<FriendAccount> listOfFriendsFromServer = new ArrayList<>();

        var executor = Executors.newSingleThreadExecutor();
        for(String pubID : allFriends){

            var future = executor.submit(() -> serverAPI.getFriend(pubID));
            FriendAccount toAdd = null;

            try {
                toAdd = future.get();
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(toAdd == null){
                Log.i("toADD is ", "null");
            } else {
                Log.i("toADD is ", "valid?");
            }

            listOfFriendsFromServer.add(toAdd);
        }

        //Example way of adding friend to UI, shown below.
        //TODO: Make sure same friend UIDs are not added to server several times.
//        for(var i : listOfFriendsFromServer) {
//            if(i.getPublicID())
//                viewModel.addFriend(i);
//
//        }

        //If no data is already saved, then adds needed friends to the database.
        viewModel.getFriendItems().observe(this, friends -> {
            for(FriendAccount i : listOfFriendsFromServer) {
                if (i != null && friends != null) {
                    // Adds friend if its not in view.
                    if (friends.contains(i) == false) {
                        viewModel.addFriend(i);
                    }

                }
            }
        });


//        //If no data is already saved, then adds three friends to the database.
//        viewModel.getFriendItems().observe(this, friends -> {
//            if (friends.size() == 0) {
//                viewModel.addFriend(new FriendAccount("Parents", new LatLong(10, 10)));
//                viewModel.addFriend(new FriendAccount("Friends", new LatLong(10, 10)));
//                viewModel.addFriend(new FriendAccount("My Home", new LatLong(10, 10)));
//            }
//        });



        //Sets up the recycler view, so that each empty/stored label gets displayed on the UI, in the
        //format given by label_input_format.xml
        recyclerView = findViewById(R.id.friendInputItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    /**
     * Currently only takes in hard coded values. TODO:: When Add Friend Button is Added, Adjust This
     *
     * @return all friends which are needed from the server.
     */
    public List<String> getNeededPublicIDs(){
        List<String> tempArrayList = new ArrayList<String>();
        tempArrayList.add("Group-7-Test-1");
        tempArrayList.add("Group-7-Test-2");

        return tempArrayList;
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