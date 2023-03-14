package com.cse110.team7.socialcompass;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.server.LabeledLocationRepository;
import com.cse110.team7.socialcompass.utils.Alert;

import java.util.UUID;


public class MainActivity extends AppCompatActivity  {
    private EditText nameEditText;
    private TextView uidTextView;
    private Button okButton;
    private LabeledLocation userLabeledLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText = findViewById(R.id.nameEditText);
        uidTextView = findViewById(R.id.uidTextView);
        okButton = findViewById(R.id.okButton);

        var database = SocialCompassDatabase.getInstance(this);
        var repo = new LabeledLocationRepository(database.getLabeledLocationDao());

        var preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        var userPublicCode = preferences.getString("userPublicCode", null);

        if (userPublicCode != null) {
            Log.i(MainActivity.class.getName(), "user public code exists: " + userPublicCode);
            userLabeledLocation = repo.selectLocalLabeledLocationWithoutLiveData(userPublicCode);

            if (userLabeledLocation != null) {
                Log.i(MainActivity.class.getName(), "user labeled location exists");
                nameEditText.setText(userLabeledLocation.getLabel());
                uidTextView.setText(userLabeledLocation.getPublicCode());
            }
        }

        nameEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (actionId != EditorInfo.IME_ACTION_DONE) {
                return false;
            }

            var label = nameEditText.getText().toString();
            Log.i(MainActivity.class.getName(), "user modified label to " + label);

            if (label.isBlank()) {
                Log.w(MainActivity.class.getName(), "user modified label is blank");
                Alert.show(this, "name cannot be empty!");
                return false;
            }

            if (userLabeledLocation == null) {
                Log.i(MainActivity.class.getName(), "generate new user labeled location");
                userLabeledLocation = new LabeledLocation.Builder()
                        .setPublicCode(UUID.randomUUID().toString())
                        .setPrivateCode(UUID.randomUUID().toString())
                        .build();
                uidTextView.setText(userLabeledLocation.getPublicCode());
            }

            if (!userLabeledLocation.getLabel().equals(label)) {
                Log.i(MainActivity.class.getName(), "user label is updated");
                userLabeledLocation.setLabel(label);

                Alert.show(this, "your private code is " + userLabeledLocation.getPrivateCode());

                repo.syncedUpsert(userLabeledLocation);
            }

            return true;
        });
    }

    public void onOkButtonClicked(View view) {
        if (userLabeledLocation == null || userLabeledLocation.getLabel().isBlank()) {
            Log.i(MainActivity.class.getName(), "user labeled location does not exist or label is blank");
            Alert.show(this, "you need to input a valid user information!");
            return;
        }

        var preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        var editor = preferences.edit();

        editor.putString("userPublicCode", userLabeledLocation.getPublicCode());
        editor.apply();

        TextView mockEndpoint = findViewById(R.id.mockEndpointView);
        String mockEndpointStr = mockEndpoint.getText().toString();

        Intent intent = new Intent(this, CompassActivity.class);
        intent.putExtra("endpoint", mockEndpointStr);

        startActivity(intent);
    }

    @VisibleForTesting
    public EditText getNameEditText() {
        return nameEditText;
    }

    @VisibleForTesting
    public TextView getUidTextView() {
        return uidTextView;
    }

    @VisibleForTesting
    public Button getOkButton() {
        return okButton;
    }
}