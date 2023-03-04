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

        assertEquals(testName, test.getName());
        assertEquals(testLocation, test.getLocation());
        assertEquals(test.getPublicID().hashCode(), test.getId());
    }

    @Test
    public void testSecondHouseConstructor() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);
        String testPublicID = "111-111-111";

        House test = new House(testName, testLocation, testPublicID);

        assertEquals(testName, test.getName());
        assertEquals(testLocation, test.getLocation());
        assertEquals(testPublicID.hashCode(), test.getId());
        assertEquals(testPublicID, test.getPublicID());
    }

    @Test
    public void testSetName() {

        String testName = "To Test";

        House test = new House("name", null);

        test.setName(testName);

        assertEquals(test.getName(), testName);
    }

    @Test
    public void testSetLocation() {

        LatLong testLocation = new LatLong(1.2, 3.1);

        House test = new House("test", null);

        test.setLocation(testLocation);

        assertEquals(test.getLocation(), testLocation);
    }
}
