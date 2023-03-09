package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import java.util.ArrayList;

public class Compass {

    private ArrayList<LabelInformation> elements;
    private float azimuth;

    public Compass(ImageView northView) {
        FriendAccount north = new FriendAccount("North", new LatLong(90, 0));
        this.elements = new ArrayList<>();
        this.elements.add(new LabelInformation(north, northView, null));
        this.azimuth = 0;
    }

    public ArrayList<LabelInformation> getElements() {
        return elements;
    }

    public LabelInformation getNorthElementDisplay() {
        return elements.get(0);
    }

    public void setElements(ArrayList<LabelInformation> elements) {
        this.elements = elements;
    }

    public void add(LabelInformation labelInformation) {
        elements.add(labelInformation);
    }

    public void updateBearingForAll(LatLong currentLocation) {
        elements.forEach(labelInformation -> labelInformation.updateBearing(currentLocation));
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void updateAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public void updateRotationForAll() {
        elements.forEach(this::updateRotation);
    }

    public void updateRotation(LabelInformation labelInformation) {
        ConstraintLayout.LayoutParams imageBasicLayout = (ConstraintLayout.LayoutParams) labelInformation.getDotView().getLayoutParams();
        imageBasicLayout.circleAngle = labelInformation.getBearing() - azimuth;
        labelInformation.getDotView().setLayoutParams(imageBasicLayout);
    }

}
