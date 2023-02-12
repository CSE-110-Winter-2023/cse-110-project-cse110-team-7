package com.cse110.team7.socialcompass.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cse110.team7.socialcompass.R;
import com.cse110.team7.socialcompass.models.Compass;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.Label;
import com.cse110.team7.socialcompass.models.LatLong;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

public class CompassActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        ImageView northLabelImageView = (ImageView) findViewById(R.id.labelNorth);
        Label northLabel = new Label(null, northLabelImageView);

        ArrayList<House> allHouses = new ArrayList<>();
        Compass thisCompass = new Compass(northLabel, allHouses);

        thisCompass.updateRotation(northLabel, 0);


//        //Setting Initial Label to targetOrientation -- Refactored into the above lines.
//
//        //If we make an individual ImageView for each of the labels, we can just use this code.
//        int targetOrientationNorth = 45; //Points straight to the top for now.
//
//        ConstraintLayout.LayoutParams basicLayout = (ConstraintLayout.LayoutParams) northLabel.getLayoutParams();
//        basicLayout.circleAngle = targetOrientationNorth;
//        northLabel.setLayoutParams(basicLayout);

        //Code for Creating New Labels W/O XML (needs to be refactored) - top two would be parameters.
        float targetOrientation2 = 180; //Points straight to the bottom.
        String labelName = "Parents";

        House parentsHouse = plotHouse(labelName);
        allHouses.add(parentsHouse);
    }

    public House plotHouse(String labelStr) {
        ImageView labelDot = new ImageView(this);

        labelDot.setId(View.generateViewId());
        labelDot.setImageResource(R.drawable.blue_circle);

        //New TextView:
        TextView labelText = new TextView(this);

        labelText.setId(View.generateViewId());
        labelText.setText(labelStr);
        labelText.setTextSize(20); //Change size of text here.
        labelText.setTypeface(null, Typeface.BOLD);
        labelText.setTextColor(Color.BLACK);

        Label houseLabel = new Label(labelText, labelDot);
        // HARDCODED LOCATION FOR NOW
        House newHouse = new House(houseLabel, new LatLong(100, 100));

        float orientation = newHouse.calculateAnge();
        //Pulls Primary Constraint from activity_compass.xml
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.compassActivityParentConstraints);

        //Adds the newDot to the back of the Views
        layout.addView(labelDot, -1);
        layout.addView(labelText, -1);

        //Note that for now all paremeters are hardcoded, but this may break on differing device sizes.
        ConstraintLayout.LayoutParams newLabelsParemeters = (ConstraintLayout.LayoutParams) labelDot.getLayoutParams();

        newLabelsParemeters.circleConstraint = R.id.CompassCenter;
        newLabelsParemeters.circleRadius = 380;
        newLabelsParemeters.circleAngle = orientation;
        newLabelsParemeters.width = 60;
        newLabelsParemeters.height = 60;

        labelDot.setLayoutParams(newLabelsParemeters);


        ConstraintLayout.LayoutParams newLabelsTextParemeters = (ConstraintLayout.LayoutParams) labelText.getLayoutParams();

        newLabelsTextParemeters.circleConstraint = R.id.CompassCenter;
        newLabelsTextParemeters.circleRadius = 440;
        newLabelsTextParemeters.circleAngle = orientation;

        labelText.setLayoutParams(newLabelsTextParemeters);
        return newHouse;
    }
}