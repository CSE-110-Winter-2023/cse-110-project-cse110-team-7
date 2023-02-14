package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.Test;

public class HouseClassUnitTests {
    @Test
    public void testHouseConstructor() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        House test = new House(testName, testLocation);

        assertEquals(test.getName(), testName);
        assertEquals(test.getLocation(), testLocation);
    }

    @Test
    public void testSetName() {

        String testName = "To Test";

        House test = new House(null, null);

        test.setName(testName);

        assertEquals(test.getName(), testName);
    }

    @Test
    public void testSetLocation() {

        LatLong testLocation = new LatLong(1.2, 3.1);

        House test = new House(null, null);

        test.setLocation(testLocation);

        assertEquals(test.getLocation(), testLocation);
    }
}
