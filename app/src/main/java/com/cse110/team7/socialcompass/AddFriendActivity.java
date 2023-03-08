package com.cse110.team7.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.backend.LocationAPI;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.utils.ShowAlert;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AddFriendActivity extends AppCompatActivity {
    private LocationAPI locationAPI;
    private Future<FriendAccount> future;
    private FriendAccount friendAccount;
    private EditText addUID;
    private String publicID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        this.locationAPI = LocationAPI.provide();
        loadMyUID();
    }

    private void loadMyUID(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        TextView myUIDView = findViewById(R.id.displayUID);
        String data = preferences.getString("UID", "N/A");
        myUIDView.setText("Your UID: " + data);
    }

    public void addFriend(String publicID) throws ExecutionException, InterruptedException {
        if(publicID.isEmpty()) {
            ShowAlert.alert(this, "Please enter a UID.");
            return;
        }

        future = locationAPI.getFriendAsync(publicID);

        friendAccount = future.get();

        if(friendAccount == null) {
            ShowAlert.alert(this, "Invalid UID.");
            return;
        }

        FriendDatabase friendDao = FriendDatabase.getInstance(getApplicationContext());
        final FriendAccountDao db = friendDao.getFriendDao();
        db.insertFriend(friendAccount);

        addUID.getText().clear();
    }

    public void onAddFriendBtnClicked(View view) throws ExecutionException, InterruptedException {
        addUID = findViewById(R.id.promptUID);
        publicID = addUID.getText().toString();
        addFriend(publicID);
    }

    public void onBackBtnClicked(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }
}