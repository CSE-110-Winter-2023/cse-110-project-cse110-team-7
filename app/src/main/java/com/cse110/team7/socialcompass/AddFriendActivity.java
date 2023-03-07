package com.cse110.team7.socialcompass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
}