package com.cse110.team7.socialcompass.models;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cse110.team7.socialcompass.R;
import com.cse110.team7.socialcompass.ui.ElementDisplay;

import java.util.ArrayList;

public class Compass {
    ElementDisplay northLabel;
    ArrayList<ElementDisplay> allElements;

    public Compass(ImageView northLabel) {
        this.northLabel = new ElementDisplay(northLabel, new LatLong(90, 0));
        allElements = new ArrayList<>();
    }

    public ElementDisplay getNorthLabel() {
        return northLabel;
    }

    public ArrayList<ElementDisplay> getAllElements() {
        return allElements;
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

        for(ElementDisplay i : allElements){
            updateRotation(i, 90);
        }
    }

    public void insert(ElementDisplay elem) {
        allElements.add(elem);
    }
}
