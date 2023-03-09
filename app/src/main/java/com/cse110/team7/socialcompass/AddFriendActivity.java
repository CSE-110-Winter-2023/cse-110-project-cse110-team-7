package com.cse110.team7.socialcompass;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

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

import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.server.LabeledLocationRepository;
import com.cse110.team7.socialcompass.server.ServerAPI;
import com.cse110.team7.socialcompass.utils.Alert;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AddFriendActivity extends AppCompatActivity {
    private EditText friendUIDEditText;
    private TextView userUIDTextView;
    private Button addFriendButton;
    private String friendUID;
    private LabeledLocationRepository repo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        friendUIDEditText = findViewById(R.id.friendUIDEditText);
        userUIDTextView = findViewById(R.id.userUIDTextView);
        addFriendButton = findViewById(R.id.add_friend_button);

        var preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        var userPublicCode = preferences.getString("userPublicCode", null);

        if (userPublicCode != null) {
            userUIDTextView.setText(getString(R.string.add_friend_display_user_uid, userPublicCode));
        }

        var database = SocialCompassDatabase.getInstance(this);
        var labeledLocationDao = database.getLabeledLocationDao();
        repo = new LabeledLocationRepository(labeledLocationDao);

        friendUIDEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                return false;
            }

            var nextFriendUID = friendUIDEditText.getText().toString();
            Log.i(AddFriendActivity.class.getName(), "adding friend with uid: " + nextFriendUID);

            if (nextFriendUID.isBlank()) {
                Log.w(AddFriendActivity.class.getName(), "friend uid is blank");
                Alert.show(this, "friend uid cannot be empty!");
                return false;
            }

            friendUID = nextFriendUID;

            return true;
        });
    }

    public void onAddFriendButtonClicked(View view) {
        if (friendUID == null || friendUID.isBlank()) {
            Log.w(AddFriendActivity.class.getName(), "friend uid is blank");
            Alert.show(this, "friend uid cannot be empty!");
            return;
        }

        var friendLabeledLocationFuture = ServerAPI.getInstance().asyncGetLabeledLocation(friendUID);

        try {
            var friendLabeledLocation = friendLabeledLocationFuture.get(5, TimeUnit.SECONDS);

            if (friendLabeledLocation == null) {
                Alert.show(this, "friend UID does not exist on server!");
                return;
            }

            repo.upsertLocalLabeledLocation(friendLabeledLocation);
            finish();
        } catch (Exception exception) {
            exception.printStackTrace();
            Alert.show(this, "unexpected exception occurred!");
        }
    }

    @VisibleForTesting
    public EditText getFriendUIDEditText() {
        return friendUIDEditText;
    }

    @VisibleForTesting
    public TextView getUserUIDTextView() {
        return userUIDTextView;
    }

    @VisibleForTesting
    public Button getAddFriendButton() {
        return addFriendButton;
    }

    @VisibleForTesting
    public String getFriendUID() {
        return friendUID;
    }
}