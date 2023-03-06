package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.utils.DistanceCalculator;
import com.cse110.team7.socialcompass.utils.DistanceFilter;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DistanceFilterUnitTests {
    private static final LatLong GEISEL_LIBRARY = new LatLong(32.88148155647509, -117.23758930927985);
    private static final LatLong COMPUTER_SCIENCE_BUILDING = new LatLong(32.882094236177636, -117.23359818236852);
    private static final LatLong EMPIRE_STATE_BUILDING = new LatLong(40.74850650324053, -73.98567044458987);


    @Test
    public void testFilterEmptyList() {
        LatLong currentLocation = new LatLong(0, 0);
        List<FriendAccount> friendAccounts = Collections.emptyList();

        assertEquals(0, DistanceFilter.filterFriends(
                friendAccounts,
                currentLocation,
                0,
                100
        ).size());
    }

    @Test
    public void testFilterFriendInRange() {
        LatLong currentLocation = GEISEL_LIBRARY;
        List<FriendAccount> friendAccounts = List.of(
                new FriendAccount("Bob", COMPUTER_SCIENCE_BUILDING)
        );

        assertEquals(1, DistanceFilter.filterFriends(
                friendAccounts,
                currentLocation,
                0,
                500
        ).size());
    }

    @Test
    public void testFilterFriendNotInRange() {
        LatLong currentLocation = GEISEL_LIBRARY;
        List<FriendAccount> friendAccounts = List.of(
                new FriendAccount("Bob", COMPUTER_SCIENCE_BUILDING),
                new FriendAccount("James", EMPIRE_STATE_BUILDING)
        );

        assertEquals(1, DistanceFilter.filterFriends(
                friendAccounts,
                currentLocation,
                0,
                500
        ).size());
    }

    @Test
    public void testFilterAtMinDistance() {
        LatLong currentLocation = GEISEL_LIBRARY;
        List<FriendAccount> friendAccounts = List.of(
                new FriendAccount("Bob", GEISEL_LIBRARY)
        );

        assertEquals(1, DistanceFilter.filterFriends(
                friendAccounts,
                currentLocation,
                0,
                100
        ).size());
    }

    /**
     * This test is probably unnecessary because it is nearly impossible
     * for a distance to lie exactly at the max distance value
     */
    @Test
    public void testFilterAtMaxDistance() {
        LatLong currentLocation = GEISEL_LIBRARY;
        List<FriendAccount> friendAccounts = List.of(
                new FriendAccount("Bob", COMPUTER_SCIENCE_BUILDING)
        );

        System.out.println(DistanceCalculator.calculateDistance(currentLocation, friendAccounts.get(0)));

        assertEquals(0, DistanceFilter.filterFriends(
                friendAccounts,
                currentLocation,
                0,
                378
        ).size());
    }
}
