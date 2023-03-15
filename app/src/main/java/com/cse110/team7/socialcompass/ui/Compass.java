package com.cse110.team7.socialcompass.ui;


import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
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

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Represents a compass on screen
 */
public class Compass {
    private static final int OFFSET = 64;
    private final LifecycleOwner lifecycleOwner;
    private final Context context;
    private final ConstraintLayout constraintLayout;
    private final ImageView compassImageView;
    private final double minDistance;
    private final double maxDistance;
    private final Map<String, LabeledLocationDisplay> labeledLocationDisplayMap;
    private final Map<LabeledLocationDisplay, View> displayConstraintView;
    private Coordinate currentCoordinate;
    private double currentOrientation;
    // the radius of scale 1 compass
    private int radius;
    private double scale;
    private boolean isHidden;
    private boolean isLastCompass;

    /**
     * @param lifecycleOwner - The Compass Activity
     * @param constraintLayout - Constraint Layout of where Compasses will be situated
     * @param minDistance - Minimum distance for circle range (in miles)
     * @param maxDistance - Maximum distance for circle range (in miles)
     */
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
        this.displayConstraintView = new HashMap<>();
        this.currentCoordinate = new Coordinate(0, 0);
        this.currentOrientation = 0;
        this.radius = 0;
        this.scale = 0;
        this.isHidden = false;
        this.isLastCompass = false;

        setupCompassImageView();

        LabeledLocation test1 = new LabeledLocation.Builder()
                .setPublicCode(UUID.randomUUID().toString())
                .setPrivateCode(UUID.randomUUID().toString())
                .setLabel("TEST_1")
                .setLatitude(40)
                .setLongitude(-120)
                .build();
        LabeledLocation test2 = new LabeledLocation.Builder()
                .setPublicCode(UUID.randomUUID().toString())
                .setPrivateCode(UUID.randomUUID().toString())
                .setLabel("TEST_2")
                .setLatitude(40)
                .setLongitude(-121)
                .build();
        LabeledLocation test3 = new LabeledLocation.Builder()
                .setPublicCode(UUID.randomUUID().toString())
                .setPrivateCode(UUID.randomUUID().toString())
                .setLabel("TEST_3")
                .setLatitude(40)
                .setLongitude(-121.4)
                .build();

        LabeledLocationDisplay temp1 = createLabeledLocationDisplay();
        temp1.setLabeledLocation(test1);
        temp1.getLabelView().setText("TEST_1");
        labeledLocationDisplayMap.put(test1.getPublicCode(), temp1);

        LabeledLocationDisplay temp2 = createLabeledLocationDisplay();
        temp2.getLabelView().setText("TEST_2");
        temp2.setLabeledLocation(test2);
        labeledLocationDisplayMap.put(test2.getPublicCode(), temp2);
/*
        LabeledLocationDisplay temp3 = createLabeledLocationDisplay();
        temp3.getLabelView().setText("TEST_3");
        temp3.setLabeledLocation(test3);
        labeledLocationDisplayMap.put(test3.getPublicCode(), temp3);
*/
    }

    /**
     * Create a new image view in the constraint layout representing the compass
     */
    public void setupCompassImageView() {

        Log.i(Compass.class.getName(), getCompassTag() + ": creating circle of size: " + radius * scale);

        compassImageView.setId(View.generateViewId());
        compassImageView.setBackground(AppCompatResources.getDrawable(context, R.drawable.circle));
        constraintLayout.addView(compassImageView, -1);

        var layoutParams = (ConstraintLayout.LayoutParams) compassImageView.getLayoutParams();

        layoutParams.width = (int) (radius * scale);
        layoutParams.height = (int) (radius * scale);
        layoutParams.startToStart = constraintLayout.getId();
        layoutParams.endToEnd = constraintLayout.getId();
        layoutParams.topToTop = constraintLayout.getId();
        layoutParams.bottomToBottom = constraintLayout.getId();

        compassImageView.setLayoutParams(layoutParams);
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
        Log.i(Compass.class.getName(), getCompassTag() + ": update labeled location displays with radius " + radius + " and scale " + scale);
        labeledLocationDisplayMap.values().forEach(labeledLocationDisplay -> {
            var layoutParams = (ConstraintLayout.LayoutParams) labeledLocationDisplay.getDotView().getLayoutParams();
            layoutParams.circleRadius = (int) (radius * scale) - OFFSET;
            labeledLocationDisplay.getDotView().setLayoutParams(layoutParams);
        });

        updateLayout();
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
     * Update the given labeled location display based on the distance and the range of the compass
     *
     * @param labeledLocationDisplay the labeled location display to be updated
     */
    public void updateLabeledLocationDisplayInRange(LabeledLocationDisplay labeledLocationDisplay) {
        Log.i(Compass.class.getName(), getCompassTag() + ": determine in range or not for labeled location display " + labeledLocationDisplay.getLabeledLocation().getLabel());

        // do not update when current compass is hidden, will be updated once the compass is not hidden anymore
        if (isHidden) {
            Log.i(Compass.class.getName(), getCompassTag() + ": current compass is hidden");
            return;
        }

        boolean isInRange = DistanceFilter.isLabeledLocationInRange(
                labeledLocationDisplay.getLabeledLocation().getCoordinate(),
                currentCoordinate,
                minDistance, maxDistance
        );

        if (isInRange) {
            Log.i(Compass.class.getName(), getCompassTag() + ": labeled location display is in range, set to visible");
            labeledLocationDisplay.getLabelView().setVisibility(View.VISIBLE);
            labeledLocationDisplay.getDotView().setVisibility(View.VISIBLE);

            var layoutParams = (ConstraintLayout.LayoutParams) labeledLocationDisplay.getDotView().getLayoutParams();
            layoutParams.circleRadius = (int) (radius * scale) - OFFSET;
            labeledLocationDisplay.getDotView().setLayoutParams(layoutParams);
            return;
        }

        Log.i(Compass.class.getName(), getCompassTag() + ": labeled location display is not in range, set label to invisible");

        labeledLocationDisplay.getLabelView().setVisibility(View.INVISIBLE);

        boolean isFurtherThanMaxDistance = DistanceFilter.isLabeledLocationFartherThanMaxDistance(
                labeledLocationDisplay.getLabeledLocation().getCoordinate(),
                currentCoordinate,
                maxDistance
        );

        if (isFurtherThanMaxDistance && isLastCompass) {
            Log.i(Compass.class.getName(), getCompassTag() + ": labeled location display is further than max distance and current compass is last compass, set dot to visible");
            labeledLocationDisplay.getDotView().setVisibility(View.VISIBLE);

            var layoutParams = (ConstraintLayout.LayoutParams) labeledLocationDisplay.getDotView().getLayoutParams();
            layoutParams.circleRadius = (int) (radius * scale);
            labeledLocationDisplay.getDotView().setLayoutParams(layoutParams);
        } else {
            Log.i(Compass.class.getName(), getCompassTag() + ": labeled location display is not further than max distance, set dot to invisible");
            labeledLocationDisplay.getDotView().setVisibility(View.INVISIBLE);
        }

        updateLayout();
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

        updateLayout();
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

        updateLayout();
    }

    public void updateLayout() {
        displayConstraintView.keySet().forEach(labeledLocationDisplay -> {
            var labelViewLayoutParam = (ConstraintLayout.LayoutParams) labeledLocationDisplay.getLabelView().getLayoutParams();
            labelViewLayoutParam.topToBottom = labeledLocationDisplay.getDotView().getId();
            labelViewLayoutParam.startToStart = labeledLocationDisplay.getDotView().getId();
            labelViewLayoutParam.endToEnd = labeledLocationDisplay.getDotView().getId();
            labelViewLayoutParam.topMargin = 0;
            labeledLocationDisplay.getLabelView().setLayoutParams(labelViewLayoutParam);
            if (displayConstraintView.get(labeledLocationDisplay) != labeledLocationDisplay.getDotView())
                constraintLayout.removeView(displayConstraintView.get(labeledLocationDisplay));
        });

        if (labeledLocationDisplayMap.size() <= 1) return;

        double stackAngle = 9;

        labeledLocationDisplayMap.values().forEach(labeledLocationDisplay -> {
            if (labeledLocationDisplay.getLabelView().getVisibility() == TextView.VISIBLE) {
                displayConstraintView.computeIfAbsent(labeledLocationDisplay, LabeledLocationDisplay::getDotView);
            } else {
                displayConstraintView.remove(labeledLocationDisplay);
            }
        });

        PriorityQueue<LabeledLocationDisplay> sortedDisplays = new PriorityQueue<>();

        sortedDisplays.addAll(displayConstraintView.keySet());

        ArrayList<Pair<LabeledLocationDisplay, LabeledLocationDisplay>> neighboringPairs = new ArrayList<>();
        var temp = new ArrayList<>(sortedDisplays);

        for(int i = 1; i < temp.size(); i++) {
            if (temp.get(i).getBearing() - temp.get(i - 1).getBearing() <= stackAngle)
                neighboringPairs.add(new Pair<LabeledLocationDisplay, LabeledLocationDisplay>
                        (temp.get(i - 1), temp.get(i)));
        }
        if (temp.size() > 2 && temp.get(temp.size() - 1).getBearing() - temp.get(0).getBearing() <= stackAngle)
            neighboringPairs.add(new Pair<LabeledLocationDisplay, LabeledLocationDisplay>
                    (temp.get(temp.size() -1), temp.get(0)));

        if (neighboringPairs.size() < 1) return;

        ArrayList<ArrayList<LabeledLocationDisplay>> neighboringGroups = new ArrayList<>();
        neighboringGroups.add(new ArrayList<>());
        neighboringGroups.get(0).add(neighboringPairs.get(0).first);
        neighboringGroups.get(0).add(neighboringPairs.get(0).second);
        int index = 0;

        for(int i = 1; i < neighboringPairs.size(); i++) {
            if (neighboringPairs.get(i -1).second == neighboringPairs.get(i).first) {
                neighboringGroups.get(index).add(neighboringPairs.get(i).second);
            } else {
                index++;
                neighboringGroups.add(new ArrayList<>());
                neighboringGroups.get(index).add(neighboringPairs.get(i).first);
                neighboringGroups.get(index).add(neighboringPairs.get(i).second);
            }
        }
        if (neighboringGroups.size() > 1 && neighboringPairs.get(neighboringPairs.size() - 1).second == neighboringPairs.get(0).first) {
            neighboringGroups.get(0).addAll(0, neighboringGroups.get(neighboringGroups.size() - 1));
            neighboringGroups.remove(neighboringGroups.size() - 1);
        }

        for(var neighboringGroup : neighboringGroups) {
            var dotViewLayoutParamFirst = (ConstraintLayout.LayoutParams) neighboringGroup.get(0).getDotView().getLayoutParams();
            var dotViewLayoutParamLast = (ConstraintLayout.LayoutParams) neighboringGroup.get(neighboringGroup.size() - 1).getDotView().getLayoutParams();

            ImageView stackConstraint = new ImageView(context);
            stackConstraint.setId(View.generateViewId());
            stackConstraint.setBackground(AppCompatResources.getDrawable(context, R.drawable.circle));
            stackConstraint.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            stackConstraint.setVisibility(ImageView.INVISIBLE);

            constraintLayout.addView(stackConstraint, -1);

            var dotViewParameters = (ConstraintLayout.LayoutParams) stackConstraint.getLayoutParams();

            dotViewParameters.circleConstraint = constraintLayout.getId();
            dotViewParameters.circleRadius = dotViewLayoutParamFirst.circleRadius;
            dotViewParameters.circleAngle = (dotViewLayoutParamFirst.circleAngle + dotViewLayoutParamLast.circleAngle) / 2;
            dotViewParameters.width = 60;
            dotViewParameters.height = 60;

            stackConstraint.setLayoutParams(dotViewParameters);

            int count = 0;
            for (var labeledLocationDisplay : neighboringGroup) {
                System.out.println(labeledLocationDisplay.getLabelView().getText());
                displayConstraintView.put(labeledLocationDisplay, stackConstraint);
                var labelViewLayoutParam = (ConstraintLayout.LayoutParams) labeledLocationDisplay.getLabelView().getLayoutParams();
                labelViewLayoutParam.topToBottom = stackConstraint.getId();
                labelViewLayoutParam.startToStart = stackConstraint.getId();
                labelViewLayoutParam.endToEnd = stackConstraint.getId();
                labelViewLayoutParam.topMargin = 48 * count++;
                labeledLocationDisplay.getLabelView().setLayoutParams(labelViewLayoutParam);
            }
        };
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

        labelView.setTextSize(16);
        labelView.setTypeface(null, Typeface.BOLD);
        labelView.setTextColor(Color.BLACK);
        labelView.setShadowLayer(6, 1, 1, Color.WHITE);

        if (isHidden) {
            labelView.setVisibility(View.INVISIBLE);
            dotView.setVisibility(View.INVISIBLE);
        }

        constraintLayout.addView(dotView, 1);
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
        labelParameters.endToEnd = dotView.getId();
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
        return "Compass: [" + minDistance + ", " + maxDistance + ")";
    }


}
