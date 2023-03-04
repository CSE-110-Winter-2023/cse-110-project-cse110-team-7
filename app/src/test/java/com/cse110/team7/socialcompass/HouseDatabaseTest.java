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

import com.cse110.team7.socialcompass.backend.HouseDao;
import com.cse110.team7.socialcompass.backend.HouseDatabase;
import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class HouseDatabaseTest {
    private HouseDao houseDao;
    private HouseDatabase houseDatabase;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        houseDatabase = Room.inMemoryDatabaseBuilder(context, HouseDatabase.class)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        houseDao = houseDatabase.getHouseDao();
    }

    @After
    public void closeDatabase() {
        houseDatabase.close();
    }

    @Test
    public void testInsertHouse() {
        House house1 = new House("Parent's", new LatLong(12, 24));
        House house2 = new House("Best friend's", new LatLong(24, -48));

        long id1 = houseDao.insertHouse(house1);
        long id2 = houseDao.insertHouse(house2);

        assertNotEquals(id1, id2);
        assertEquals(house1.getPublicID().hashCode(), id1);
        assertEquals(house2.getPublicID().hashCode(), id2);
    }

    @Test
    public void testSelectHouse() {
        House house1 = new House("Best friend's", new LatLong(24, -48));

        long id1 = houseDao.insertHouse(house1);

        House insertedHouse = houseDao.selectHouse(id1);

        assertEquals(id1, insertedHouse.getId());
        assertEquals(house1.getName(), insertedHouse.getName());
        assertEquals(house1.getLocation(), insertedHouse.getLocation());
    }

    @Test
    public void testUpdateHouse() {
        House house1 = new House("Best friend's", new LatLong(24, -48));

        long id1 = houseDao.insertHouse(house1);

        House insertedHouse = houseDao.selectHouse(id1);

        insertedHouse.setName("Parent's");
        insertedHouse.setLocation(new LatLong(12, 0));

        assertEquals(1, houseDao.updateHouse(insertedHouse));

        House updatedHouse = houseDao.selectHouse(id1);

        assertNotNull(updatedHouse);
        assertEquals("Parent's", updatedHouse.getName());
        assertEquals(new LatLong(12, 0), updatedHouse.getLocation());
    }

    @Test
    public void testDeleteHouse() {
        House house1 = new House("Best friend's", new LatLong(24, -48));

        long id1 = houseDao.insertHouse(house1);

        House insertedHouse = houseDao.selectHouse(id1);

        assertEquals(1, houseDao.deleteHouse(insertedHouse));

        assertNull(houseDao.selectHouse(id1));
    }
}
