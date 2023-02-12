package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;
import android.widget.TextView;

public class HouseDisplayView {
    private final ImageView dotView;
    private final TextView labelView;

    public HouseDisplayView(ImageView dotView, TextView labelView) {
        this.dotView = dotView;
        this.labelView = labelView;
    }

    public ImageView getDotView() {
        return dotView;
    }

    public TextView getLabelView() {
        return labelView;
    }

    public void setDotViewRotation(double rotationAngle) {

    }

    public void setLabelRotation(double rotationAngle) {

    }
}
