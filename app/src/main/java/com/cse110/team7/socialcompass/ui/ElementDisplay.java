package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.House;

public class ElementDisplay {

    private final House house;
    private ImageView dotView;
    private TextView labelView;
    private float bearing;

    public ElementDisplay(House house, ImageView dotView, TextView labelView) {
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

}
