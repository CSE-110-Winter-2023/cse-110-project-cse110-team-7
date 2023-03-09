package com.cse110.team7.socialcompass.ui;

import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.utils.AngleCalculator;

/**
 * Contains all the information that goes into representing any individual label.
 */
public class LabelInformation {

    private final FriendAccount friendAccount;
    private ImageView dotView;
    private TextView labelView;
    private float bearing;

    public LabelInformation(FriendAccount friendAccount, ImageView dotView, TextView labelView) {
        this.friendAccount = friendAccount;
        this.dotView = dotView;
        this.labelView = labelView;
        this.bearing = 0;
    }

    public FriendAccount getFriend() {
        return friendAccount;
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
        bearing = AngleCalculator.calculateAngle(currentLocation, friendAccount);
    }

}
