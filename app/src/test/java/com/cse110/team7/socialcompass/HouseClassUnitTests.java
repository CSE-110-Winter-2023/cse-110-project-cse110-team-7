package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import android.widget.ImageView;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.ui.ElementDisplay;

import org.junit.Test;

public class HouseClassUnitTests {
    @Test
    public void testHouseConstructor() {

        ImageView labelImage = new ImageView(null);

        labelImage.setId(1);
        labelImage.setImageResource(R.drawable.blue_circle);

        LatLong testLatLong = new LatLong(11.1, 13.2);

        ElementDisplay testElementDisplay = new ElementDisplay(labelImage, testLatLong);

        House house = new House(testElementDisplay, testLatLong);

        assertEquals(house.getLocation(), testLatLong);
        assertEquals(house.getHouseDisplay(), testElementDisplay);
    }

    @Test
    public void testSetLocation() {
        House house = new House(null, null);

        LatLong testLatLong = new LatLong(1.2, 3.1);

        house.setLocation(testLatLong);

        assertEquals(house.getLocation(), testLatLong);
    }

    @Test
    public void testSetDisplay() {
        House house = new House(null, null);

        ImageView labelImage = new ImageView(null);

        labelImage.setId(1);
        labelImage.setImageResource(R.drawable.blue_circle);

        LatLong testLatLong = new LatLong(5.1, 7.2);

        ElementDisplay testElementDisplay = new ElementDisplay(labelImage, testLatLong);

        house.setDisplay(testElementDisplay);

        assertEquals(house.getHouseDisplay(), testElementDisplay);
    }
}
