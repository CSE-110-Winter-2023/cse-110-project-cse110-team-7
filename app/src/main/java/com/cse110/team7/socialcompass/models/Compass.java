package com.cse110.team7.socialcompass.models;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cse110.team7.socialcompass.R;
import com.cse110.team7.socialcompass.ui.ElementDisplay;

import java.util.ArrayList;

public class Compass {
    ElementDisplay northLabel;
    ArrayList<House> allHouses;

    public Compass(ElementDisplay northLabel, ArrayList<House> allHouses) {
        this.northLabel = northLabel;
        this.allHouses = allHouses;
    }

    public Compass(ImageView northLabel) {
        this.northLabel = new ElementDisplay(null, northLabel);
    }

    public ElementDisplay getNorthLabel() {
        return northLabel;
    }

    public ArrayList<House> getAllHouses() {
        return allHouses;
    }

    //To Do:
    public void updateRotation(ElementDisplay toUpdate, float updateValue) {
        TextView labelText = toUpdate.getLabelView();
        ImageView labelImage = toUpdate.getDotView();


        if(labelText != null) {
            //Update Text Value:
            ConstraintLayout.LayoutParams textBasicLayout = (ConstraintLayout.LayoutParams) labelText.getLayoutParams();
            textBasicLayout.circleAngle = updateValue;
            labelText.setLayoutParams(textBasicLayout);
        }

        //Update Image Value:
        ConstraintLayout.LayoutParams imageBasicLayout = (ConstraintLayout.LayoutParams) labelImage.getLayoutParams();
        imageBasicLayout.circleAngle = updateValue;
        labelImage.setLayoutParams(imageBasicLayout);
    }

    //Need to add actual update values:
    public void updateAllLabels(){
        updateRotation(northLabel, 0);

        for(House i : allHouses){
            updateRotation(i.getHouseDisplay(), 90);
        }
    }

    public void insertHouse(House h) {
        allHouses.add(h);
    }
}
