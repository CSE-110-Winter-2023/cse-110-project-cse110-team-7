package com.cse110.team7.socialcompass.models;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class Compass {
    Label northLabel;
    ArrayList<House> allHouses;

    public Compass(Label northLabel, ArrayList<House> allHouses) {
        this.northLabel = northLabel;
        this.allHouses = allHouses;
    }

    public Label getNorthLabel() {
        return northLabel;
    }

    public ArrayList<House> getAllHomes() {
        return allHouses;
    }

    //To Do:
    public void updateRotation(Label toUpdate, float updateValue) {
        TextView labelText = toUpdate.getLabelName();
        ImageView labelImage = toUpdate.getLabelImageView();


        //Update Text Value:
        ConstraintLayout.LayoutParams textBasicLayout = (ConstraintLayout.LayoutParams) labelText.getLayoutParams();
        textBasicLayout.circleAngle = updateValue;
        labelText.setLayoutParams(textBasicLayout);

        //Update Image Value:
        ConstraintLayout.LayoutParams imageBasicLayout = (ConstraintLayout.LayoutParams) labelImage.getLayoutParams();
        imageBasicLayout.circleAngle = updateValue;
        labelImage.setLayoutParams(imageBasicLayout);
    }

    //Need to add actual update values:
    public void updateAllLabels(){
        updateRotation(northLabel, 0);

        for(House i : allHouses){
            updateRotation(i.getHouseLabel(), 90);
        }
    }
}
