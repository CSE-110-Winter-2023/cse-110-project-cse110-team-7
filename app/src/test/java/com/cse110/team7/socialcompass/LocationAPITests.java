package com.cse110.team7.socialcompass;

import static org.junit.Assert.*;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.cse110.team7.socialcompass.backend.LocationAPI;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

public class LocationAPITests {
    FriendAccount testLoc1 = new FriendAccount("Mom", new LatLong(0, 10));
    FriendAccount testLoc2 = new FriendAccount("Dad", new LatLong(10, 10));
    FriendAccount testLoc3 = new FriendAccount("Friend", new LatLong(10, 0));


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
        locAPI.deleteFriend(testLoc1);
        locAPI.deleteFriend(testLoc2);
        locAPI.deleteFriend(testLoc3);
    }

    @Test
    /* verifies ability to put and get locations */
    public void testGetFriend() throws ExecutionException, InterruptedException, TimeoutException {

        Future<FriendAccount> futureFriend = locAPI.getFriendAsync(testLoc1.getPublicID());

        FriendAccount h = futureFriend.get(1, SECONDS);
        assertEquals(testLoc1, h);
    }

    @Test
    public void testUpdateLocation() throws ExecutionException, InterruptedException, TimeoutException {
        testLoc1.setLocation(new LatLong(90, 90));
        locAPI.updateLocation(testLoc1);

        Future<FriendAccount> futureFriend = locAPI.getFriendAsync(testLoc1.getPublicID());
        FriendAccount h = futureFriend.get(1, SECONDS);
        assertEquals(testLoc1, h);
    }

    @Test
    public void testUpdateName() throws ExecutionException, InterruptedException, TimeoutException {
        testLoc1.setName("new name");
        locAPI.updateName(testLoc1);

        Future<FriendAccount> futureHouse = locAPI.getFriendAsync(testLoc1.getPublicID());
        FriendAccount h = futureHouse.get(1, SECONDS);
        assertEquals(testLoc1, h);
    }

    @Test
    public void testGetAllAndPublish() throws ExecutionException, InterruptedException, TimeoutException {
        List<FriendAccount> serverFriendAccounts = locAPI.getAllFriends();
        assertFalse(serverFriendAccounts.contains(testLoc1));
        assertFalse(serverFriendAccounts.contains(testLoc2));
        assertFalse(serverFriendAccounts.contains(testLoc3));

        locAPI.publish(testLoc1);
        locAPI.publish(testLoc3);

        serverFriendAccounts = locAPI.getAllFriends();

        assertTrue(serverFriendAccounts.contains(testLoc1));
        assertFalse(serverFriendAccounts.contains(testLoc2));
        assertTrue(serverFriendAccounts.contains(testLoc3));

    }
}
