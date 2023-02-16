package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.Test;

public class LatLongClassUnitTests {
    @Test
    public void testLatLongConstructor() {
        LatLong testLatLong = new LatLong(20.1, 15.6);
        assertEquals(20.1, testLatLong.getLatitude(), 0);
        assertEquals(15.6, testLatLong.getLongitude(), 0);
    }

    @Test
    public void testLatLongString() {
        LatLong testLatLong = new LatLong(11.1, 13.2);

        String expectedLatLongStr = "11.1,13.2";

        assertEquals(testLatLong.toString(), expectedLatLongStr);
    }
}
