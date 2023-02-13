package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import android.widget.ImageView;

import com.cse110.team7.socialcompass.models.House;
import com.cse110.team7.socialcompass.ui.Compass;
import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.ui.ElementDisplay;

import org.junit.Test;

public class CompassClassUnitTests {
    @Test
    public void testCompassConstructor() {
        ImageView img = new ImageView(null);

        img.setId(1);
        img.setImageResource(R.drawable.n);

        Compass testCompass = new Compass(img);

        assertEquals(testCompass.getNorthElementDisplay().getDotView(), img);
        assertEquals(testCompass.getNorthElementDisplay().getHouse().getLocation(), new LatLong(90, 0));
        assertEquals(testCompass.getElements().size(), 1);
    }

    @Test
    public void testAdd() {
        String testName = "Test";
        LatLong testLocation = new LatLong(11.1, 13.2);

        House test = new House(testName, testLocation);
        ElementDisplay testElement = new ElementDisplay(test, null, null);

        Compass testCompass = new Compass(null);
        testCompass.add(testElement);

        assertEquals(testCompass.getElements().get(1), testElement);
        assertEquals(testCompass.getElements().size(), 2);
    }
}
