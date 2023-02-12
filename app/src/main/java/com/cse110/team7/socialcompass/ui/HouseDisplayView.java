package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.House;

public class HouseDisplayView {

    private final House house;
    private final ImageView dotView;
    private final TextView labelView;

    private float bearing;

    public HouseDisplayView(House house, ImageView dotView, TextView labelView) {
        this.house = house;
        this.dotView = dotView;
        this.labelView = labelView;
        this.bearing = 0.0f;
    }

    public House getHouse() {
        return house;
    }

    public ImageView getDotView() {
        return dotView;
    }

    public TextView getLabelView() {
        return labelView;
    }

    public float getBearing() {
        return bearing;
    }

    public void updateBearing(float currentBearing) {
        bearing = currentBearing;
    }

}
