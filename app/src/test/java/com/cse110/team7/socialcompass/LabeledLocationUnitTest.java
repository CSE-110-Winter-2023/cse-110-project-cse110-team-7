package com.cse110.team7.socialcompass;


import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.cse110.team7.socialcompass.models.Coordinate;
import com.cse110.team7.socialcompass.models.LabeledLocation;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class LabeledLocationUnitTest {
    private static final String PUBLIC_CODE = UUID.randomUUID().toString();
    private static final String PRIVATE_CODE = UUID.randomUUID().toString();
    private static final String LABEL = "test-label";
    private static final double LATITUDE = 12;
    private static final double LONGITUDE = 20;
    private static final boolean IS_LISTED_PUBLICLY = false;
    private static final long CREATED_AT = Instant.now().getEpochSecond();
    private static final long UPDATED_AT = Instant.now().getEpochSecond();

    @Test
    public void testConstructor() {
        LabeledLocation labeledLocation = new LabeledLocation(
                PUBLIC_CODE, PRIVATE_CODE,
                LABEL,
                LATITUDE, LONGITUDE,
                IS_LISTED_PUBLICLY,
                CREATED_AT, UPDATED_AT
        );

        Assert.assertEquals(PUBLIC_CODE, labeledLocation.getPublicCode());
        Assert.assertEquals(PRIVATE_CODE, labeledLocation.getPrivateCode());
        Assert.assertEquals(LABEL, labeledLocation.getLabel());
        Assert.assertEquals(LATITUDE, labeledLocation.getLatitude(), 0.001);
        Assert.assertEquals(LONGITUDE, labeledLocation.getLongitude(), 0.001);
        Assert.assertEquals(IS_LISTED_PUBLICLY, labeledLocation.isListedPublicly());
        Assert.assertEquals(CREATED_AT, labeledLocation.getCreatedAt());
        Assert.assertEquals(UPDATED_AT, labeledLocation.getUpdatedAt());
    }

    @Test
    public void testBuilder() {
        LabeledLocation labeledLocation = new LabeledLocation.Builder()
                .setPublicCode(PUBLIC_CODE)
                .setPrivateCode(PRIVATE_CODE)
                .setLabel(LABEL)
                .setLatitude(LATITUDE)
                .setLongitude(LONGITUDE)
                .setListedPublicly(IS_LISTED_PUBLICLY)
                .setCreatedAt(CREATED_AT)
                .setUpdatedAt(UPDATED_AT)
                .build();

        Assert.assertEquals(PUBLIC_CODE, labeledLocation.getPublicCode());
        Assert.assertEquals(PRIVATE_CODE, labeledLocation.getPrivateCode());
        Assert.assertEquals(LABEL, labeledLocation.getLabel());
        Assert.assertEquals(LATITUDE, labeledLocation.getLatitude(), 0.001);
        Assert.assertEquals(LONGITUDE, labeledLocation.getLongitude(), 0.001);
        Assert.assertEquals(IS_LISTED_PUBLICLY, labeledLocation.isListedPublicly());
        Assert.assertEquals(CREATED_AT, labeledLocation.getCreatedAt());
        Assert.assertEquals(UPDATED_AT, labeledLocation.getUpdatedAt());
    }

    @Test
    public void testGetCoordinate() {
        LabeledLocation labeledLocation = new LabeledLocation.Builder()
                .setPublicCode(PUBLIC_CODE)
                .setPrivateCode(PRIVATE_CODE)
                .setLabel(LABEL)
                .setLatitude(LATITUDE)
                .setLongitude(LONGITUDE)
                .setListedPublicly(IS_LISTED_PUBLICLY)
                .setCreatedAt(CREATED_AT)
                .setUpdatedAt(UPDATED_AT)
                .build();

        var coordinate = labeledLocation.getCoordinate();

        Assert.assertEquals(LATITUDE, coordinate.latitude, 0.001);
        Assert.assertEquals(LONGITUDE, coordinate.longitude, 0.001);
    }

    @Test
    public void testSetCoordinate() {
        LabeledLocation labeledLocation = new LabeledLocation.Builder()
                .setPublicCode(PUBLIC_CODE)
                .setPrivateCode(PRIVATE_CODE)
                .setLabel(LABEL)
                .setLatitude(LATITUDE)
                .setLongitude(LONGITUDE)
                .setListedPublicly(IS_LISTED_PUBLICLY)
                .setCreatedAt(CREATED_AT)
                .setUpdatedAt(UPDATED_AT)
                .build();

        var coordinate = new Coordinate(15, 80);

        labeledLocation.setCoordinate(coordinate);

        Assert.assertEquals(coordinate.latitude, labeledLocation.getLatitude(), 0.001);
        Assert.assertEquals(coordinate.longitude, labeledLocation.getLongitude(), 0.001);
    }
}
