package com.cse110.team7.socialcompass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendAccountRepository;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.backend.LocationAPI;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.services.LocationService;


/*
 * First page of our application; we should probably move all of this over to another activity.
 */
import com.cse110.team7.socialcompass.utils.ShowAlert;

import java.util.ArrayList;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;


public class MainActivity extends AppCompatActivity  {
    public RecyclerView recyclerView;
    EditText nameView;
    TextView uidView;
    Button okButton;
    InputDisplayAdapter adapter;
    InputDisplayViewModel viewModel;

    LocationAPI serverAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Tracks interactions between the UI and the database, allowing us to update values as they
        //get changed.
        viewModel = new ViewModelProvider(this).get(InputDisplayViewModel.class);
        nameView = findViewById(R.id.nameTextView);
        uidView = findViewById(R.id.UIDtextView);
        okButton = findViewById(R.id.goToCompass);

        nameView.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                return false;
            }

            String name = nameView.getText().toString();

            if (name.isBlank()) {
                ShowAlert.alert(this, "name cannot be empty");
                return false;
            }

            saveProfile();
            return true;
        });

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
        /*recyclerView = findViewById(R.id.friendInputItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);*/
        loadProfile();
    }

    public void saveProfile(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();
        uidView.setText(UUID.randomUUID().toString());
        editor.putString("myName", nameView.getText().toString());
        editor.putString("myPublicID", uidView.getText().toString());
        editor.apply();
    }

    public void loadProfile(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String n = preferences.getString("myName", "");
        String s = preferences.getString("myPublicID", "N/A");
        nameView.setText(n);
        uidView.setText(s);
    }

    /**
     * On button click, only goes to CompassActivity if at least one location has been inputted.
     */
    public void onGoToCompass(View view) {
        //float orientation;
        if (nameView.getText().toString().isBlank()) {
            ShowAlert.alert(this, "name cannot be empty");
            return;
        }

        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
        /*Intent intent = new Intent(this, CompassActivity.class);
        //intent.putExtra("orientation", orientation);

        for(FriendAccount i : adapter.friendAccountList) {
            if (i.getLocation() != null) {
                startActivity(intent);
                return;
            }
        }*/
    }

    /**
     * Currently only takes in hard coded values. TODO:: When Add Friend Button is Added, Adjust This
     *
     * @return all friends which are needed from the server.
     */
    public ArrayList<String> getNeededPubIDs(){
        ArrayList<String> friendArrayList = new ArrayList<String>();
        FriendDatabase friendDao = FriendDatabase.getInstance(getApplicationContext());
        final FriendAccountDao db = friendDao.getFriendDao();
        db.selectFriends().observe(this, houses -> {
            for(FriendAccount i : houses){
                if(i.getLocation() != null){
                    friendArrayList.add(i.getPublicID());
                }
            }
        });

        return friendArrayList;
    }

    @Override
    protected void onDestroy() {
        saveProfile();
        super.onDestroy();
    }

    public EditText getNameView() {
        return nameView;
    }

    public TextView getUidView() {
        return uidView;
    }

    public Button getOkButton() {
        return okButton;
    }
}