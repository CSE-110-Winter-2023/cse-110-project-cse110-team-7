package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;
import android.widget.TextView;

public class ElementDisplay {
    private final ImageView dotView;
    private final TextView labelView;

    public ElementDisplay(TextView labelView, ImageView dotView) {
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
