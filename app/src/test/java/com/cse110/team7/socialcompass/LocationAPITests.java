package com.cse110.team7.socialcompass;

import static org.junit.Assert.*;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.cse110.team7.socialcompass.backend.LocationAPI;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class LocationAPITests {
    House testLoc1 = new House("Mom", new LatLong(0, 10));
    House testLoc2 = new House("Dad", new LatLong(10, 10));
    House testLoc3 = new House("Friend", new LatLong(10, 0));


    LocationAPI locAPI;

    @Before
    /* populate server */
    public void setup() {
        locAPI = LocationAPI.provide();
        locAPI.putLocation(testLoc1);
        // print private IDs- locations must be manually deleted if program crashes
        System.err.println(testLoc1.getPublicID() + " : " + testLoc1.getPrivateID());
        locAPI.putLocation(testLoc2);
        System.err.println(testLoc2.getPublicID() + " : " + testLoc2.getPrivateID());
        locAPI.putLocation(testLoc3);
        System.err.println(testLoc3.getPublicID() + " : " + testLoc3.getPrivateID());

    }

    @After
    public void cleanServer() {
        locAPI.deleteHouse(testLoc1);
        locAPI.deleteHouse(testLoc2);
        locAPI.deleteHouse(testLoc3);
    }

    @Test
    /* verifies ability to put and get locations */
    public void testGetHouse() throws ExecutionException, InterruptedException, TimeoutException {

        Future<House> futureHouse = locAPI.getHouseAsync(testLoc1.getPublicID());

        House h = futureHouse.get(1, SECONDS);
        assertEquals(testLoc1, h);
    }

    @Test
    public void testUpdateLocation() throws ExecutionException, InterruptedException, TimeoutException {
        testLoc1.setLocation(new LatLong(90, 90));
        locAPI.updateLocation(testLoc1);

        Future<House> futureHouse = locAPI.getHouseAsync(testLoc1.getPublicID());
        House h = futureHouse.get(1, SECONDS);
        assertEquals(testLoc1, h);
    }

    @Test
    public void testUpdateName() throws ExecutionException, InterruptedException, TimeoutException {
        testLoc1.setName("new name");
        locAPI.updateName(testLoc1);

        Future<House> futureHouse = locAPI.getHouseAsync(testLoc1.getPublicID());
        House h = futureHouse.get(1, SECONDS);
        assertEquals(testLoc1, h);
    }

    @Test
    public void testGetAllAndPublish() throws ExecutionException, InterruptedException, TimeoutException {
        List<House> serverHouses = locAPI.getAllHouses();
        assertFalse(serverHouses.contains(testLoc1));
        assertFalse(serverHouses.contains(testLoc2));
        assertFalse(serverHouses.contains(testLoc3));

        locAPI.publish(testLoc1);
        locAPI.publish(testLoc3);

        serverHouses = locAPI.getAllHouses();

        assertTrue(serverHouses.contains(testLoc1));
        assertFalse(serverHouses.contains(testLoc2));
        assertTrue(serverHouses.contains(testLoc3));

    }
}
