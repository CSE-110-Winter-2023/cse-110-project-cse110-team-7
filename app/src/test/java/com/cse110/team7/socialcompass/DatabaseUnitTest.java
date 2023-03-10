package com.cse110.team7.socialcompass;


import android.database.sqlite.SQLiteConstraintException;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.database.LabeledLocationDao;
import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.cse110.team7.socialcompass.models.LabeledLocation;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseUnitTest {
    private static final LabeledLocation testLocation1 = new LabeledLocation.Builder().build();
    private static final LabeledLocation testLocation2 = new LabeledLocation.Builder().build();
    private SocialCompassDatabase socialCompassDatabase;
    private LabeledLocationDao labeledLocationDao;

    @Before
    public void createDatabase() {
        var context = ApplicationProvider.getApplicationContext();

        SocialCompassDatabase.injectTestDatabase(
                Room.inMemoryDatabaseBuilder(context, SocialCompassDatabase.class)
                    .allowMainThreadQueries()
                    .build()
        );

        socialCompassDatabase = SocialCompassDatabase.getInstance(context);
        labeledLocationDao = socialCompassDatabase.getLabeledLocationDao();
    }

    @After
    public void destroyDatabase() {
        labeledLocationDao.deleteLabeledLocation(testLocation1);
        labeledLocationDao.deleteLabeledLocation(testLocation2);
        socialCompassDatabase.close();
    }

    @Test
    public void testInsertLabeledLocation() {
        var rowId = labeledLocationDao.upsertLabeledLocation(testLocation1);
        Assert.assertTrue(rowId > 0);
    }

    @Test
    public void testSelectLabeledLocation() {
        labeledLocationDao.upsertLabeledLocation(testLocation1);

        var getLabeledLocationResult = labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation1.getPublicCode());

        Assert.assertNotNull(getLabeledLocationResult);
        Assert.assertEquals(testLocation1.getPublicCode(), getLabeledLocationResult.getPublicCode());

        getLabeledLocationResult = labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation2.getPublicCode());

        Assert.assertNull(getLabeledLocationResult);
    }

    @Test
    public void testUpdateLabeledLocation() {
        labeledLocationDao.upsertLabeledLocation(testLocation1);

        testLocation1.setLabel("wow");

        labeledLocationDao.upsertLabeledLocation(testLocation1);

        var getLabeledLocationResult = labeledLocationDao.selectLabeledLocationWithoutLiveData(testLocation1.getPublicCode());

        Assert.assertEquals(testLocation1.getLabel(), getLabeledLocationResult.getLabel());
    }

    @Test
    public void testDeleteLabeledLocation() {
        var deletedRowCount = labeledLocationDao.deleteLabeledLocation(testLocation1);

        Assert.assertEquals(deletedRowCount, 0);

        labeledLocationDao.upsertLabeledLocation(testLocation1);

        deletedRowCount = labeledLocationDao.deleteLabeledLocation(testLocation1);

        Assert.assertEquals(deletedRowCount, 1);
    }

    @Test
    public void testSelectLabeledLocations() {
        labeledLocationDao.upsertLabeledLocation(testLocation1);
        labeledLocationDao.upsertLabeledLocation(testLocation2);

        var selectLabeledLocationsResult = labeledLocationDao.selectLabeledLocationsWithoutLiveData();

        Assert.assertEquals(2, selectLabeledLocationsResult.size());
    }
}
