package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.Test;

public class FriendAccountClassUnitTests {
    @Test
    public void testHouseConstructor() {

        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        FriendAccount test = new FriendAccount(testName, testLocation);

        assertEquals(test.getName(), testName);
        assertEquals(test.getLocation(), testLocation);
    }

    @Test
    public void testSetName() {

        String testName = "To Test";

        FriendAccount test = new FriendAccount("name", null);

        test.setName(testName);

        assertEquals(test.getName(), testName);
    }

    @Test
    public void testSetLocation() {

        LatLong testLocation = new LatLong(1.2, 3.1);

        FriendAccount test = new FriendAccount("test", null);

        test.setLocation(testLocation);

        assertEquals(test.getLocation(), testLocation);
    }
}
