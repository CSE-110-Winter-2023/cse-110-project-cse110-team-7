package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import android.widget.ImageView;

import com.cse110.team7.socialcompass.models.Compass;
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

        assertEquals(testCompass.getNorthLabel().getDotView(), img);
        assertEquals(testCompass.getNorthLabel().getLocation(), new LatLong(90, 0));
        assertEquals(testCompass.getAllElements().size(), 0);
    }

    @Test
    public void testElementInsert() {
        ElementDisplay testElement = new ElementDisplay("House", null);

        Compass testCompass = new Compass(null);
        testCompass.insert(testElement);

        assertEquals(testCompass.getAllElements().get(0), testElement);
        assertEquals(testCompass.getAllElements().get(0).getLabelName(), "House");
        assertEquals(testCompass.getAllElements().size(), 1);
    }
}
