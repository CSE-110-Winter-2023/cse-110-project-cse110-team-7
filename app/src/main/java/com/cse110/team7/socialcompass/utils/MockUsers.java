package com.cse110.team7.socialcompass.utils;

import com.cse110.team7.socialcompass.backend.LocationAPI;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/* simulates moving users for testing */
public class MockUsers {
    public FriendAccount mockMom = new FriendAccount("Mom", new LatLong(37.556378, -86.321884));
    public FriendAccount mockDad = new FriendAccount("Dad", new LatLong(32.900333, -117.250120));
    public FriendAccount mockFriend = new FriendAccount("Emma", new LatLong(32.782270, -117.075287));

    public int[] latMovements = {1, 0, -1, 0};
    public int[] longMovements = {0, -1, 0, 1};
    int movementIndex;

    public volatile static MockUsers instance = null;

    private ScheduledFuture<?> future;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();


    private LocationAPI locAPI;

    public MockUsers() {
        mockMom.privateID = "445-544-9843";
        mockDad.privateID = "445-544-9844";
        mockFriend.privateID = "445-544-9845";
        movementIndex = 0;
        locAPI = new LocationAPI();
        locAPI.putLocation(mockMom);
        locAPI.putLocation(mockDad);
        locAPI.putLocation(mockFriend);
    }

    public static MockUsers provide() {
        if (instance == null) {
            instance = new MockUsers();
        }
        return instance;
    }

    public void move() {
        int i = 0;
        this.future = executor.scheduleAtFixedRate(() -> {
            nextLocation(mockMom);
            nextLocation(mockDad);
            nextLocation(mockFriend);
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void nextLocation(FriendAccount friendAccount) {
        LatLong curLoc = friendAccount.getLocation();
        double newLat = curLoc.getLatitude()+latMovements[movementIndex];
        double newLong = curLoc.getLongitude()+longMovements[movementIndex];
        friendAccount.setLocation(new LatLong(newLat, newLong));
        movementIndex = (movementIndex + 1) % longMovements.length;
        locAPI.updateLocation(friendAccount);
    }

    public void close() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.schedule(() -> {
            locAPI.deleteHouse(mockMom);
            locAPI.deleteHouse(mockDad);
            locAPI.deleteHouse(mockFriend);
            executor.shutdown();
        }, 20, TimeUnit.SECONDS);

    }


}
