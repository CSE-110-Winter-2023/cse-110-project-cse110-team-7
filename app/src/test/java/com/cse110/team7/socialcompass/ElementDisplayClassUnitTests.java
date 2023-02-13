package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;

import android.widget.ImageView;
import android.widget.TextView;

import com.cse110.team7.socialcompass.models.LatLong;
import com.cse110.team7.socialcompass.ui.ElementDisplay;

import org.junit.Test;

public class ElementDisplayClassUnitTests {
    @Test
    public void testAlternateConstructor() {
        LatLong testLatLong = new LatLong(1,2);
        ElementDisplay testElementDisplay = new ElementDisplay("test", testLatLong);

        assertEquals(testElementDisplay.getLabelName(), "test");
        assertEquals(testElementDisplay.getLocation(), testLatLong);
    }

    @Test
    public void testSetLocation() {
        ElementDisplay testElementDisplay = new ElementDisplay("", null);

        LatLong testLatLong = new LatLong(5, 10);

        testElementDisplay.setLocation(testLatLong);

        assertEquals(testElementDisplay.getLocation(), testLatLong);
    }

    @Test
    public void testSetDotView() {
        ElementDisplay testElementDisplay = new ElementDisplay("", null);

        ImageView labelImage = new ImageView(null);

        labelImage.setId(1);
        labelImage.setImageResource(R.drawable.blue_circle);

        testElementDisplay.setDotView(labelImage);

        assertEquals(testElementDisplay.getDotView(), labelImage);
    }

    @Test
    public void testSetLabelView() {
        ElementDisplay testElementDisplay = new ElementDisplay("", null);

        TextView txt = new TextView(null);

        txt.setId(1);
        txt.setText("test");

        testElementDisplay.setLabelView(txt);

        assertEquals(testElementDisplay.getLabelView(), txt);
    }

    @Test
    public void testUpdateBearing() {
        ElementDisplay testElementDisplay = new ElementDisplay("", null);

        testElementDisplay.updateBearing(100);

        assertEquals(testElementDisplay.getBearing(), 100, 0);
    }
}
