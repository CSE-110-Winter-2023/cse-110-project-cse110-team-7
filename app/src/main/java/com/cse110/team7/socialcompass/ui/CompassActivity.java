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

import org.xmlpull.v1.XmlPullParser;

public class CompassActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);


        //Getting North (needs to be refactored):
        ImageView northLabel = (ImageView) findViewById(R.id.labelNorth);

        //Setting Initial Label to targetOrientation

        //If we make an individual ImageView for each of the labels, we can just use this code.
        int targetOrientationNorth = 45; //Points straight to the top for now.

        ConstraintLayout.LayoutParams basicLayout = (ConstraintLayout.LayoutParams) northLabel.getLayoutParams();
        basicLayout.circleAngle = targetOrientationNorth;
        northLabel.setLayoutParams(basicLayout);



        //Code for Creating New Labels W/O XML (needs to be refactored) - top two would be parameters.
        float targetOrientation2 = 180; //Points straight to the bottom.
        String labelName = "Parents";

        //Creates New ImageView
        ImageView newDot = new ImageView(this);

        newDot.setId(View.generateViewId());
        newDot.setImageResource(R.drawable.blue_circle);

        //New TextView:
        TextView newText = new TextView(this);

        newText.setId(View.generateViewId());
        newText.setText(labelName);
        newText.setTextSize(20); //Change size of text here.
        newText.setTypeface(null, Typeface.BOLD);
        newText.setTextColor(Color.BLACK);

        //Pulls Primary Constraint from activity_compass.xml
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.compassActivityParentConstraints);

        //Adds the newDot to the back of the Views
        layout.addView(newDot, -1);
        layout.addView(newText, -1);

        //Note that for now all paremeters are hardcoded, but this may break on differing device sizes.
        ConstraintLayout.LayoutParams newLabelsParemeters = (ConstraintLayout.LayoutParams) newDot.getLayoutParams();

        newLabelsParemeters.circleConstraint = R.id.CompassCenter;
        newLabelsParemeters.circleRadius = 380;
        newLabelsParemeters.circleAngle = targetOrientation2;
        newLabelsParemeters.width = 60;
        newLabelsParemeters.height = 60;

        newDot.setLayoutParams(newLabelsParemeters);


        ConstraintLayout.LayoutParams newLabelsTextParemeters = (ConstraintLayout.LayoutParams) newText.getLayoutParams();

        newLabelsTextParemeters.circleConstraint = R.id.CompassCenter;
        newLabelsTextParemeters.circleRadius = 440;
        newLabelsTextParemeters.circleAngle = targetOrientation2;

        newText.setLayoutParams(newLabelsTextParemeters);
    }
}