package com.cse110.team7.socialcompass.ui;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.cse110.team7.socialcompass.R;
import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.utils.AngleCalculator;
import com.cse110.team7.socialcompass.utils.DistanceFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a compass on screen
 */
public class Compass {
    private final LifecycleOwner lifecycleOwner;
    private final Context context;
    private final ConstraintLayout constraintLayout;
    private final ImageView compassImageView;
    private final double minDistance;
    private final double maxDistance;
    private final Map<String, LabeledLocationDisplay> labeledLocationDisplayMap;
    private Coordinate currentCoordinate;
    private double currentOrientation;
    private int radius;

//    we might need these for iteration 2
//    private double scale;
//    private boolean isHidden;
//    private boolean isLastCompass;

    public Compass(
            LifecycleOwner lifecycleOwner,
            ConstraintLayout constraintLayout,
            double minDistance, double maxDistance
    ) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = constraintLayout.getContext();
        this.constraintLayout = constraintLayout;
        this.compassImageView = new ImageView(this.context);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.labeledLocationDisplayMap = new HashMap<>();
        this.currentCoordinate = new Coordinate(0, 0);
        this.currentOrientation = 0;
        this.radius = 0;

        setupCompassImageView();
    }

    /**
     * Create a new image view in the constraint layout representing the compass
     */
    public void setupCompassImageView() {
        compassImageView.setId(View.generateViewId());
        compassImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.circle));
        constraintLayout.addView(compassImageView, -1);

        var layoutParams = (ConstraintLayout.LayoutParams) compassImageView.getLayoutParams();

        layoutParams.width = 0;
        layoutParams.height = 0;
        layoutParams.startToStart = constraintLayout.getId();
        layoutParams.endToEnd = constraintLayout.getId();
        layoutParams.topToTop = constraintLayout.getId();
        layoutParams.bottomToBottom = constraintLayout.getId();

        compassImageView.setLayoutParams(layoutParams);
    }

    /**
     * Update the distance from all location displays to the center of the compass
     */
    public void updateLabeledLocationDisplay() {
        Log.i(Compass.class.getName(), getCompassTag() + ": update labeled location displays with radius " + radius);
        labeledLocationDisplayMap.values().forEach(labeledLocationDisplay -> {
            var layoutParams = (ConstraintLayout.LayoutParams) labeledLocationDisplay.getDotView().getLayoutParams();
            layoutParams.circleRadius = radius;
            labeledLocationDisplay.getDotView().setLayoutParams(layoutParams);
        });
    }

    /**
     * Update the radius of the compass
     *
     * @param radius the radius of the compass
     */
    public void setRadius(int radius) {
        Log.i(Compass.class.getName(), getCompassTag() + ": update radius to " + radius);
        this.radius = radius;
        updateLabeledLocationDisplay();
    }

    /**
     * Update the given labeled location display based on the distance and the range of the compass
     *
     * @param labeledLocationDisplay the labeled location display to be updated
     */
    public void updateLabeledLocationDisplayInRange(LabeledLocationDisplay labeledLocationDisplay) {
        Log.i(Compass.class.getName(), getCompassTag() + ": determine in range or not for labeled location display " + labeledLocationDisplay.getLabeledLocation().getLabel());

        boolean isInRange = DistanceFilter.isLabeledLocationInRange(
                labeledLocationDisplay.getLabeledLocation(),
                currentCoordinate,
                minDistance, maxDistance
        );

        if (isInRange) {
            Log.i(Compass.class.getName(), getCompassTag() + ": labeled location display is in range, set to visible");
            labeledLocationDisplay.getLabelView().setVisibility(View.VISIBLE);
            labeledLocationDisplay.getDotView().setVisibility(View.VISIBLE);
            return;
        }

        Log.i(Compass.class.getName(), getCompassTag() + ": labeled location display is not in range, set label to invisible");

        labeledLocationDisplay.getDotView().setVisibility(View.INVISIBLE);
        labeledLocationDisplay.getLabelView().setVisibility(View.INVISIBLE);
    }

    /**
     * Display the labeled location on screen with updates
     *
     * @param labeledLocationLiveData the labeled location to be displayed
     */
    public void displayLabeledLocation(LiveData<LabeledLocation> labeledLocationLiveData) {
        labeledLocationLiveData.observe(lifecycleOwner, labeledLocation -> {
            Log.i(Compass.class.getName(), getCompassTag() + ": received update from labeled location " + labeledLocation.getLabel());

            var labeledLocationDisplay = labeledLocationDisplayMap.get(labeledLocation.getPublicCode());

            if (labeledLocationDisplay == null) {
                Log.i(Compass.class.getName(), getCompassTag() + ": received update from new labeled location " + labeledLocation.getLabel() + ", create new labeled location display");
                labeledLocationDisplay = createLabeledLocationDisplay();
                labeledLocationDisplayMap.put(labeledLocation.getPublicCode(), labeledLocationDisplay);
            }

            labeledLocationDisplay.setLabeledLocation(labeledLocation);
            labeledLocationDisplay.getLabelView().setText(labeledLocation.getLabel());

            updateBearing(labeledLocationDisplay);
            updateLabeledLocationDisplayInRange(labeledLocationDisplay);
        });
    }

    /**
     * Update the given labeled location display based on the bearing
     *
     * @param labeledLocationDisplay the labeled display to be updated
     */
    public void updateBearing(LabeledLocationDisplay labeledLocationDisplay) {
        Log.d(Compass.class.getName(), getCompassTag() + ": update bearing for " + labeledLocationDisplay.getLabeledLocation().getLabel());
        labeledLocationDisplay.setBearing(AngleCalculator.calculateAngle(currentCoordinate, labeledLocationDisplay.getLabeledLocation().getCoordinate()));
        labeledLocationDisplay.updateLayoutParams(currentOrientation);

        updateLabeledLocationDisplayInRange(labeledLocationDisplay);
    }

    /**
     * Update all labeled location displays based on the coordinate
     *
     * @param currentCoordinate the current coordinate
     */
    public void updateBearingForAll(Coordinate currentCoordinate) {
        if (this.currentCoordinate.equals(currentCoordinate)) {
            return;
        }

        Log.d(Compass.class.getName(), getCompassTag() + ": update bearing for all with coordinate " + currentCoordinate);
        this.currentCoordinate = currentCoordinate;

        labeledLocationDisplayMap.values().forEach(this::updateBearing);
    }

    /**
     * Update all labeled location displays based on the orientation
     *
     * @param currentOrientation the current orientation
     */
    public void updateOrientationForAll(double currentOrientation) {
        if (Double.compare(this.currentOrientation, currentOrientation) == 0) {
            return;
        }

        Log.d(Compass.class.getName(), getCompassTag() + ": update orientation for all with orientation " + currentOrientation);
        this.currentOrientation = currentOrientation;

        labeledLocationDisplayMap.values().forEach(labeledLocationDisplay -> {
            labeledLocationDisplay.updateLayoutParams(currentOrientation);
        });
    }

    /**
     * Create a labeled location display
     *
     * @return a new labeled location display with all views created and added
     */
    @NonNull
    public LabeledLocationDisplay createLabeledLocationDisplay() {
        ImageView dotView = new ImageView(context);
        TextView labelView = new TextView(context);

        dotView.setId(View.generateViewId());
        labelView.setId(View.generateViewId());

        dotView.setBackground(AppCompatResources.getDrawable(context, R.drawable.circle));
        dotView.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        labelView.setTextSize(20);
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setTextColor(Color.WHITE);
        labelView.setShadowLayer(6, 1, 1, Color.BLACK);


        constraintLayout.addView(dotView, -1);
        constraintLayout.addView(labelView, -1);

        var dotViewParameters = (ConstraintLayout.LayoutParams) dotView.getLayoutParams();

        dotViewParameters.circleConstraint = constraintLayout.getId();
        dotViewParameters.circleRadius = radius;
        dotViewParameters.circleAngle = (float) currentOrientation;
        dotViewParameters.width = 60;
        dotViewParameters.height = 60;

        dotView.setLayoutParams(dotViewParameters);

        var labelParameters = (ConstraintLayout.LayoutParams) labelView.getLayoutParams();

        labelParameters.topToBottom = dotView.getId();
        labelParameters.startToStart = dotView.getId();
        labelParameters.startToEnd = dotView.getId();
        labelParameters.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        labelParameters.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;

        return new LabeledLocationDisplay(dotView, labelView);
    }

    /**
     * Helper method to get a special tag for the compass to indicate the range
     *
     * @return a special tag for the compass to indicate the range
     */
    private String getCompassTag() {
        return Compass.class.getName() + "[" + minDistance + ", " + maxDistance + ")";
    }
}
