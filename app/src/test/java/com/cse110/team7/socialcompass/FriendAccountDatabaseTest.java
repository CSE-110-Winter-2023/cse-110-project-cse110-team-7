package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.backend.FriendAccountDao;
import com.cse110.team7.socialcompass.backend.FriendDatabase;
import com.cse110.team7.socialcompass.models.FriendAccount;
import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FriendAccountDatabaseTest {
    private FriendAccountDao friendAccountDao;
    private FriendDatabase friendDatabase;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        friendDatabase = Room.inMemoryDatabaseBuilder(context, FriendDatabase.class)
                .allowMainThreadQueries()
                .build();

        friendAccountDao = friendDatabase.getFriendDao();
    }

    @After
    public void closeDatabase() {
        friendDatabase.close();
    }

    @Test
    public void testInsertHouse() {
        FriendAccount friendAccount1 = new FriendAccount("Parent's", new LatLong(12, 24));
        FriendAccount friendAccount2 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendAccountDao.insertFriend(friendAccount1);
        long id2 = friendAccountDao.insertFriend(friendAccount2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testSelectHouse() {
        FriendAccount friendAccount1 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendAccountDao.insertFriend(friendAccount1);

        FriendAccount insertedFriendAccount = friendAccountDao.selectFriend(id1);

        assertEquals(id1, insertedFriendAccount.getId());
        assertEquals(friendAccount1.getName(), insertedFriendAccount.getName());
        assertEquals(friendAccount1.getLocation(), insertedFriendAccount.getLocation());
    }

    @Test
    public void testUpdateHouse() {
        FriendAccount friendAccount1 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendAccountDao.insertFriend(friendAccount1);

        FriendAccount insertedFriendAccount = friendAccountDao.selectFriend(id1);

        insertedFriendAccount.setName("Parent's");
        insertedFriendAccount.setLocation(new LatLong(12, 0));

        assertEquals(1, friendAccountDao.updateFriend(insertedFriendAccount));

        FriendAccount updatedFriendAccount = friendAccountDao.selectFriend(id1);

        assertNotNull(updatedFriendAccount);
        assertEquals("Parent's", updatedFriendAccount.getName());
        assertEquals(new LatLong(12, 0), updatedFriendAccount.getLocation());
    }

    @Test
    public void testDeleteHouse() {
        FriendAccount friendAccount1 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendAccountDao.insertFriend(friendAccount1);

        FriendAccount insertedFriendAccount = friendAccountDao.selectFriend(id1);

        assertEquals(1, friendAccountDao.deleteFriend(insertedFriendAccount));

        assertNull(friendAccountDao.selectFriend(id1));
    }
}
