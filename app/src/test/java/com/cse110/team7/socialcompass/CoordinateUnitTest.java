package com.cse110.team7.socialcompass;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.models.Coordinate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CoordinateUnitTest {

    private static final double LATITUDE = 12;
    private static final double LONGITUDE = 20;

    @Test
    public void testConstructor() {
        var coordinate = new Coordinate(LATITUDE, LONGITUDE);

        Assert.assertEquals(LATITUDE, coordinate.latitude, 0.001);
        Assert.assertEquals(LONGITUDE, coordinate.longitude, 0.001);
    }

    @Test
    public void testEquals() {
        var coordinate = new Coordinate(LATITUDE, LONGITUDE);

        var otherCoordinate = new Coordinate(LATITUDE, LONGITUDE);

        Assert.assertTrue(coordinate.equals(otherCoordinate));
        Assert.assertTrue(otherCoordinate.equals(coordinate));

        Assert.assertFalse(coordinate.equals(null));
    }
}
