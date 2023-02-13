package com.cse110.team7.socialcompass.ui;

import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.LatLong;

public class ElementDisplay {
    private String name;
    private ImageView dotView;
    private TextView labelView;
    private LatLong location;
    private float bearing;


    public ElementDisplay(ImageView iv, LatLong loc) {
        dotView = iv;
        location = loc;
    }

    public ElementDisplay(String name, LatLong loc) {
        this.name = name;
        this.location = loc;
        dotView = null;
        labelView = null;

    }

    public LatLong getLocation() {
        return location;
    }

    public String getLabelName() {
        return name;
    }

    public void setLocation(LatLong location) {
        this.location = location;
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
