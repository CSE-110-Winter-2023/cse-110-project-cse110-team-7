package com.cse110.team7.socialcompass.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cse110.team7.socialcompass.R;

import org.xmlpull.v1.XmlPullParser;

public class CompassActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);


        //Getting Initial Dot
        ImageView northLabel = (ImageView) findViewById(R.id.labelNorth);

        //Setting Initial Label to targetOrientation

        //If we make an individual ImageView for each of the labels, we can use this.
        int targetOrientationNorth = 45; //Points straight to the top for now.

        ConstraintLayout.LayoutParams basicLayout = (ConstraintLayout.LayoutParams) northLabel.getLayoutParams();
        basicLayout.circleAngle = targetOrientationNorth;
        northLabel.setLayoutParams(basicLayout);



        //Code for Creating New Labels
        float targetOrientation2 = 180; //Points straight to the bottom.

        //Creates New ImageView
        ImageView newDot = new ImageView(this);

        newDot.setId(View.generateViewId());
        newDot.setImageResource(R.drawable.blue_circle);

        //Pulls Primary Constraint from activity_compass.xml
        ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.compassActivityParentConstraints);

        //Adds the newDot to the back of the Views
        layout.addView(newDot, -1);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) newDot.getLayoutParams();

        layoutParams.circleConstraint = R.id.CompassCenter;
        layoutParams.circleRadius = 380;
        layoutParams.circleAngle = targetOrientation2;
        layoutParams.width = 60;
        layoutParams.height = 60;

        newDot.setLayoutParams(layoutParams);

    }
}