package com.cse110.team7.socialcompass.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;

import com.cse110.team7.socialcompass.R;

import org.xmlpull.v1.XmlPullParser;

public class CompassActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        //Getting Initial Dot
        ImageView normalDot = (ImageView)findViewById(R.id.labelNorth);
        normalDot.setVisibility(View.VISIBLE);

        //Setting Initial Label to targetOrientation
        int targetOrientation = 90; //Points straight to the right.

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) normalDot.getLayoutParams();
        layoutParams.circleAngle = targetOrientation;
        normalDot.setLayoutParams(layoutParams);



        //Creating New Dot -- currently has error; it overwrites the first labels location
        ImageView newDot = new ImageView(this);
        newDot.setImageResource(R.drawable.labeldot);
        newDot.setScaleType(normalDot.getScaleType());
        ConstraintLayout.LayoutParams newParams = (ConstraintLayout.LayoutParams) normalDot.getLayoutParams();
        newParams.circleAngle = 180;
        newDot.setLayoutParams(newParams);
        newDot.setVisibility(View.VISIBLE);


    }
}