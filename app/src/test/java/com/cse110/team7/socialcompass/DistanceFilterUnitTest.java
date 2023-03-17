package com.cse110.team7.socialcompass;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.utils.DistanceFilter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DistanceFilterUnitTest {
    private static final Coordinate GEISEL_LIBRARY = new Coordinate(32.88148155647509, -117.23758930927985);
    private static final Coordinate COMPUTER_SCIENCE_BUILDING = new Coordinate(32.882094236177636, -117.23359818236852);
    private static final Coordinate EMPIRE_STATE_BUILDING = new Coordinate(40.74850650324053, -73.98567044458987);

    @Test
    public void testIsFartherThanMaximumDistance() {
        Assert.assertFalse(
                DistanceFilter.isLabeledLocationFartherThanMaxDistance(
                        COMPUTER_SCIENCE_BUILDING,
                        GEISEL_LIBRARY,
                        500
                )
        );

        Assert.assertTrue(
                DistanceFilter.isLabeledLocationFartherThanMaxDistance(
                        EMPIRE_STATE_BUILDING,
                        GEISEL_LIBRARY,
                        500
                )
        );
    }

    @Test
    public void testIsInRange() {
        Assert.assertFalse(
                DistanceFilter.isLabeledLocationInRange(
                        COMPUTER_SCIENCE_BUILDING,
                        GEISEL_LIBRARY,
                        1000, 2000
                )
        );

        Assert.assertTrue(
                DistanceFilter.isLabeledLocationInRange(
                        COMPUTER_SCIENCE_BUILDING,
                        GEISEL_LIBRARY,
                        0, 500
                )
        );
    }
}
