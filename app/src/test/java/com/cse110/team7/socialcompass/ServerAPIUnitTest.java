package com.cse110.team7.socialcompass;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.server.ServerAPI;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;


@RunWith(AndroidJUnit4.class)
public class ServerAPIUnitTest {
    private static final LabeledLocation testLocation1 = new LabeledLocation.Builder()
            .setLabel("Mom")
            .setLatitude(0)
            .setLongitude(10)
            .build();
    private static final LabeledLocation testLocation2 = new LabeledLocation.Builder()
            .setLabel("Dad")
            .setLatitude(10)
            .setLongitude(10)
            .build();
    private static final LabeledLocation testLocation3 = new LabeledLocation.Builder()
            .setLabel("Friend")
            .setLatitude(10)
            .setLongitude(0)
            .build();

    @After
    public void resetServer() throws ExecutionException, InterruptedException  {
        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation1).get();
        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation2).get();
        ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation3).get();
    }

    @Test
    public void testPutLocation() throws ExecutionException, InterruptedException {
        var putLocation1Result = ServerAPI.getInstance().asyncPutLabeledLocation(testLocation1).get();

        Assert.assertNotNull(putLocation1Result);
        Assert.assertEquals(putLocation1Result.getPublicCode(), testLocation1.getPublicCode());
    }

    @Test
    public void testGetLocation() throws ExecutionException, InterruptedException {
        var getLocation2Result = ServerAPI.getInstance().asyncGetLabeledLocation(testLocation2.getPublicCode()).get();

        Assert.assertNull(getLocation2Result);

        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation2).get();

        getLocation2Result = ServerAPI.getInstance().asyncGetLabeledLocation(testLocation2.getPublicCode()).get();

        Assert.assertNotNull(getLocation2Result);
        Assert.assertEquals(testLocation2.getPublicCode(), getLocation2Result.getPublicCode());
    }

    @Test
    public void testGetLocations() throws ExecutionException, InterruptedException {
        var getLocationsResult = ServerAPI.getInstance().asyncGetLabeledLocations().get();

        Assert.assertNotNull(getLocationsResult);
        Assert.assertTrue(getLocationsResult.size() > 0);
    }

    @Test
    public void testDeleteLocations() throws ExecutionException, InterruptedException {
        System.out.println(ServerAPI.getInstance().asyncPutLabeledLocation(testLocation1).get());
        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation2).get();
        ServerAPI.getInstance().asyncPutLabeledLocation(testLocation3).get();

        var deleteLocation1Result = ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation1).get();
        Assert.assertTrue(deleteLocation1Result);
        var deleteLocation2Result = ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation2).get();
        Assert.assertTrue(deleteLocation2Result);
        var deleteLocation3Result = ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation3).get();
        Assert.assertTrue(deleteLocation3Result);

        var getLocation1Result = ServerAPI.getInstance().asyncGetLabeledLocation(testLocation1.getPublicCode()).get();
        Assert.assertNull(getLocation1Result);
        var getLocation2Result = ServerAPI.getInstance().asyncGetLabeledLocation(testLocation2.getPublicCode()).get();
        Assert.assertNull(getLocation2Result);
        var getLocation3Result = ServerAPI.getInstance().asyncGetLabeledLocation(testLocation3.getPublicCode()).get();
        Assert.assertNull(getLocation3Result);

        deleteLocation1Result = ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation1).get();
        Assert.assertFalse(deleteLocation1Result);
        deleteLocation2Result = ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation2).get();
        Assert.assertFalse(deleteLocation2Result);
        deleteLocation3Result = ServerAPI.getInstance().asyncDeleteLabeledLocation(testLocation3).get();
        Assert.assertFalse(deleteLocation3Result);
    }

    @Test
    public void testPatchLocation() throws ExecutionException, InterruptedException {
        var putLocation1Result = ServerAPI.getInstance().asyncPutLabeledLocation(testLocation1).get();
        Assert.assertNotNull(putLocation1Result);

        testLocation1.setLabel("Dad");

        var patchLocation1Result = ServerAPI.getInstance().asyncPatchLabeledLocation(testLocation1).get();
        Assert.assertNotNull(patchLocation1Result);

        Assert.assertEquals(testLocation1.getPublicCode(), patchLocation1Result.getPublicCode());
        Assert.assertEquals(testLocation1.getLabel(), patchLocation1Result.getLabel());

        var patchUnknownLocationResult = ServerAPI.getInstance().asyncPatchLabeledLocation(new LabeledLocation.Builder().build()).get();
        Assert.assertNull(patchUnknownLocationResult);
    }
}
