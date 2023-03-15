package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.cse110.team7.socialcompass.models.LabeledLocation;


/**
 * Represents the labeled location views on screen
 */
public class LabeledLocationDisplay implements Comparable<LabeledLocationDisplay> {
    private final ImageView dotView;
    private final TextView labelView;
    private LabeledLocation labeledLocation;
    private double bearing;

    public LabeledLocationDisplay(@NonNull ImageView dotView, @NonNull TextView labelView) {
        this.dotView = dotView;
        this.labelView = labelView;
        this.bearing = 0;
    }

    /**
     * Update the layout param for the views based on the orientation
     * @param orientation the orientation
     */
    public void updateLayoutParams(double orientation) {
        var dotViewLayoutParam = (ConstraintLayout.LayoutParams) dotView.getLayoutParams();
        dotViewLayoutParam.circleAngle = (float) (bearing - orientation);
        dotView.setLayoutParams(dotViewLayoutParam);
    }

    @NonNull
    public ImageView getDotView() {
        return dotView;
    }

    @NonNull
    public TextView getLabelView() {
        return labelView;
    }

    public LabeledLocation getLabeledLocation() {
        return labeledLocation;
    }

    public void setLabeledLocation(LabeledLocation labeledLocation) {
        this.labeledLocation = labeledLocation;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    @Override
    public int compareTo(LabeledLocationDisplay o) {
        return Double.compare(getBearing(), o.getBearing());
    }
}
