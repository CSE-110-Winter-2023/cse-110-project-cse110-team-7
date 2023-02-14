package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

public class LabelInformation {

    private final House house;
    private ImageView dotView;
    private TextView labelView;
    private float bearing;

    public LabelInformation(House house, ImageView dotView, TextView labelView) {
        this.house = house;
        this.dotView = dotView;
        this.labelView = labelView;
        this.bearing = 0;
    }

    public House getHouse() {
        return house;
    }

    public ImageView getDotView() {
        return dotView;
    }

    public void setDotView(ImageView dotView) {
        this.dotView = dotView;
    }

    public TextView getLabelView() {
        return labelView;
    }

    public void setLabelView(TextView labelView) {
        this.labelView = labelView;
    }

    public float getBearing() {
        return bearing;
    }

    public void updateBearing(float currentBearing) {
        bearing = currentBearing;
    }

    public void updateBearing(LatLong currentLocation) {
        bearing = AngleCalculator.calculateAngle(currentLocation, house);
    }

}
