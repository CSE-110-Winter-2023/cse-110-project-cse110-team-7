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
public class FriendDatabaseTest {
    private FriendAccountDao friendDao;
    private FriendDatabase friendDatabase;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        friendDatabase = Room.inMemoryDatabaseBuilder(context, FriendDatabase.class)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        friendDao = friendDatabase.getFriendDao();
    }

    @After
    public void closeDatabase() {
        friendDatabase.close();
    }

    @Test
    public void testInsertFriend() {
        FriendAccount friend1 = new FriendAccount("Parent's", new LatLong(12, 24));
        FriendAccount friend2 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendDao.insertFriend(friend1);
        long id2 = friendDao.insertFriend(friend2);

        assertNotEquals(id1, id2);
        assertEquals(friend1.getPublicID().hashCode(), id1);
        assertEquals(friend2.getPublicID().hashCode(), id2);
    }

    @Test
    public void testSelectFriend() {
        FriendAccount friend1 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendDao.insertFriend(friend1);

        FriendAccount insertedFriend = friendDao.selectFriend(id1);

        assertEquals(id1, insertedFriend.getId());
        assertEquals(friend1.getName(), insertedFriend.getName());
        assertEquals(friend1.getLocation(), insertedFriend.getLocation());
    }

    @Test
    public void testUpdateFriend() {
        FriendAccount friend1 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendDao.insertFriend(friend1);

        FriendAccount insertedFriend = friendDao.selectFriend(id1);

        insertedFriend.setName("Parent's");
        insertedFriend.setLocation(new LatLong(12, 0));

        assertEquals(1, friendDao.updateFriend(insertedFriend));

        FriendAccount updatedFriend = friendDao.selectFriend(id1);

        assertNotNull(updatedFriend);
        assertEquals("Parent's", updatedFriend.getName());
        assertEquals(new LatLong(12, 0), updatedFriend.getLocation());
    }

    @Test
    public void testDeleteFriend() {
        FriendAccount friend1 = new FriendAccount("Best friend's", new LatLong(24, -48));

        long id1 = friendDao.insertFriend(friend1);

        FriendAccount insertedFriend = friendDao.selectFriend(id1);

        assertEquals(1, friendDao.deleteFriend(insertedFriend));

        assertNull(friendDao.selectFriend(id1));
    }
}
