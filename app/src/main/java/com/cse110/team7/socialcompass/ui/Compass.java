package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;

import java.util.ArrayList;

public class Compass {

    private ArrayList<ElementDisplay> elements;
    private float azimuth;

    public Compass(ImageView northView) {
        House north = new House("North", new LatLong(90, 0));
        this.elements = new ArrayList<>();
        this.elements.add(new ElementDisplay(north, northView, null));
        this.azimuth = 0;
    }

    public ArrayList<ElementDisplay> getElements() {
        return elements;
    }

    public ElementDisplay getNorthElementDisplay() {
        return elements.get(0);
    }

    public void setElements(ArrayList<ElementDisplay> elements) {
        this.elements = elements;
    }

    public void add(ElementDisplay elementDisplay) {
        elements.add(elementDisplay);
    }

    public void updateBearingForAll(LatLong currentLocation) {
        elements.forEach(elementDisplay -> elementDisplay.updateBearing(currentLocation));
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

    public void updateRotation(ElementDisplay elementDisplay) {
        ConstraintLayout.LayoutParams imageBasicLayout = (ConstraintLayout.LayoutParams) elementDisplay.getDotView().getLayoutParams();
        imageBasicLayout.circleAngle = elementDisplay.getBearing() - azimuth;
        elementDisplay.getDotView().setLayoutParams(imageBasicLayout);
    }

}
