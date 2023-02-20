
package com.cse110.team7.socialcompass;

import static org.junit.Assert.*;

import android.content.Context;
import android.graphics.Rect;
import android.widget.ImageView;

import androidx.lifecycle.Lifecycle;
import androidx.room.Room;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.backend.HouseDao;
import com.cse110.team7.socialcompass.backend.HouseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class US1StoryTest   {
    private HouseDao houseDao;
    private HouseDatabase houseDatabase;

    @Before
    public void createDatabase() {
        Context context = ApplicationProvider.getApplicationContext();

        houseDatabase = Room.inMemoryDatabaseBuilder(context, HouseDatabase.class)
                .allowMainThreadQueries()
                .build();

        HouseDatabase.injectTestDatabase(houseDatabase);

        houseDao = houseDatabase.getHouseDao();
    }

    @After
    public void closeDatabase() {
        houseDatabase.close();
    }

    @Test
    public void US1test() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            ImageView compass = (ImageView) activity.findViewById(R.id.compassImage);
            ImageView north = (ImageView) activity.findViewById(R.id.labelNorth);
            assertNotNull(compass);
            assertNotNull(north);
        });
    }

    @Test
    public void US1testCase2() {
        var scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);

        scenario.onActivity(activity -> {
            ImageView image = (ImageView) activity.findViewById(R.id.labelNorth);
            Rect rt = image.getDrawable().getBounds();

            int drawLeft = rt.left;
            int drawTop = rt.top;
            int drawRight = rt.right;
            int drawBottom = rt.bottom;

            assertEquals(drawLeft, 0);
            assertEquals(drawTop, 0);
            assertEquals(drawRight, 2000);
            assertEquals(drawBottom, 2000);
        });
    }
}
