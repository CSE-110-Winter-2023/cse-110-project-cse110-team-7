package com.cse110.team7.socialcompass;


import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.utils.DistanceCalculator;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DistanceCalculatorUnitTest {

    private static final Coordinate GEISEL_LIBRARY = new Coordinate(32.88148155647509, -117.23758930927985);
    private static final Coordinate COMPUTER_SCIENCE_BUILDING = new Coordinate(32.882094236177636, -117.23359818236852);

    @Test
    public void calculate() {
        double dist = DistanceCalculator.calculateDistance(GEISEL_LIBRARY, COMPUTER_SCIENCE_BUILDING);
        assertEquals(.23568236599, dist, .1);
    }
}
