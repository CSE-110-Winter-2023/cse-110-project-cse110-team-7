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
import androidx.annotation.VisibleForTesting;
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
import java.util.concurrent.CountDownLatch;

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

    private final Map<String, CountDownLatch> locationUpdateTimeMap;
    public final Map<String, LabeledLocationDisplay> labeledLocationDisplayMap;
    private Coordinate currentCoordinate;
    private double currentOrientation;
    //Radius of ...
    private int radius;
    private double scale;
    private boolean isHidden;
    private boolean isLastCompass;
    private int sizeOfCircle;

    public double circleType;

    public static final double FIRST_CIRCLE = 4;
    public static final double SECOND_CIRCLE = 2.5;
    public static final double THIRD_CIRCLE = 1.6;
    public static final double FOURTH_CIRCLE = 1.2;

//    we might need these for iteration 2
//    private double scale;
//    private boolean isHidden;
//    private boolean isLastCompass;

    /**
     * @param lifecycleOwner - The Compass Activity
     * @param constraintLayout - Constraint Layout of where Compasses will be situated
     * @param minDistance - Minimum distance for circle range (in miles)
     * @param maxDistance - Maximum distance for circle range (in miles)
     * @param scale - Size of the Compass Circle (use constants provided in Compass class)
     * @param screenSize - Minimum screen size.
     */
    public Compass(
            LifecycleOwner lifecycleOwner,
            ConstraintLayout constraintLayout,
            double minDistance, double maxDistance, double scale, int screenSize
    ) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = constraintLayout.getContext();
        this.constraintLayout = constraintLayout;
        this.compassImageView = new ImageView(this.context);
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.labeledLocationDisplayMap = new HashMap<>();
        this.locationUpdateTimeMap = new HashMap<>();
        this.currentCoordinate = new Coordinate(0, 0);
        this.currentOrientation = 0;
        this.radius = 0;
        this.circleType = scale;

        this.sizeOfCircle = (int)( screenSize / scale);


        setupCompassImageView();
    }

    /**
     * Create a new image view in the constraint layout representing the compass
     */
    public void setupCompassImageView() {

        Log.i(Compass.class.getName(), getCompassTag() + ": creating circle of size " + sizeOfCircle);

        compassImageView.setId(View.generateViewId());
        compassImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.circle));
        constraintLayout.addView(compassImageView, -1);

        var layoutParams = (ConstraintLayout.LayoutParams) compassImageView.getLayoutParams();

        layoutParams.width = sizeOfCircle;
        layoutParams.height = sizeOfCircle;
        layoutParams.startToStart = constraintLayout.getId();
        layoutParams.endToEnd = constraintLayout.getId();
        layoutParams.topToTop = constraintLayout.getId();
        layoutParams.bottomToBottom = constraintLayout.getId();

        compassImageView.setLayoutParams(layoutParams);
    }

    /**
     * Set the scale of current compass image
     *
     * @param scale the scale of current compass image
     */
    public void setScale(double scale) {
        Log.i(Compass.class.getName(), getCompassTag() + ": update scale to " + scale);
        this.scale = scale;

        updateCompassImageView();
        updateLabeledLocationDisplay();
    }

    /**
     * Set whether the current compass is hidden or not
     *
     * @param isHidden whether the current compass is hidden or not
     */
    public void setHidden(boolean isHidden) {
        if (this.isHidden == isHidden) return;

        this.isHidden = isHidden;

        // we want to set every view to invisible if compass is hidden
        // otherwise we want to set every view to visible
        int visibility = isHidden ? View.INVISIBLE : View.VISIBLE;

        compassImageView.setVisibility(visibility);
        labeledLocationDisplayMap.values().forEach(labeledLocationDisplay -> {
            labeledLocationDisplay.getDotView().setVisibility(visibility);
            labeledLocationDisplay.getLabelView().setVisibility(visibility);
        });
    }

    /**
     * Get whether the current compass is hidden or not
     *
     * @return isHidden whether the current compass is hidden or not
     */
    public boolean getHidden() {
        return isHidden;
    }



        /**
         * Set whether the current compass is the last compass
         *
         * @param isLastCompass whether the current compass is the last compass
         */
    public void setLastCompass(boolean isLastCompass) {
        this.isLastCompass = isLastCompass;

        // update all views again to display dots for locations outside of current compass range
        labeledLocationDisplayMap.values().forEach(this::updateLabeledLocationDisplayInRange);
    }

    /**
     * Resize the compass image based on the current compass scale
     */
    public void updateCompassImageView() {
        Log.i(Compass.class.getName(), getCompassTag() + ": update compass image with scale " + scale);
        compassImageView.setScaleX((float) scale);
        compassImageView.setScaleY((float) scale);
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
                labeledLocationDisplay.getLabeledLocation().getCoordinate(),
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
        Log.i(Compass.class.getName(), getCompassTag() + ": displayed element count " + labeledLocationDisplayMap.size());

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

            var countDown = locationUpdateTimeMap.get(labeledLocation.getPublicCode());

            if (countDown != null) {
                countDown.countDown();
            }
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

    public Map<String, LabeledLocationDisplay> getLabeledLocationDisplayMap() {
        return labeledLocationDisplayMap;
    }

    /**
     * Helper method to get a special tag for the compass to indicate the range
     *
     * @return a special tag for the compass to indicate the range
     */
    public String getCompassTag() {
        return Compass.class.getName() + "[" + minDistance + ", " + maxDistance + ")";
    }


    public Map<String, CountDownLatch> getLocationUpdateTimeMap() {
        return locationUpdateTimeMap;
    }

    public int getSizeOfCircle() {
        return sizeOfCircle;
    }

    @VisibleForTesting
    public int getVisibilityOfFriend(LabeledLocation loc) {
        return this.labeledLocationDisplayMap.get(loc.getPublicCode())
                .getDotView().getVisibility();
    }
}
