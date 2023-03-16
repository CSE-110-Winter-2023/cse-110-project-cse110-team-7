package com.cse110.team7.socialcompass;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static java.lang.System.out;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.cse110.team7.socialcompass.database.LabeledLocationDao;
import com.cse110.team7.socialcompass.database.SocialCompassDatabase;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.cse110.team7.socialcompass.utils.TimestampAdapter;
import com.google.gson.annotations.JsonAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Tests the read and write methods of the TimestampAdapter, ensuring conversion to JSON is working
 * correctly.
 */
@RunWith(RobolectricTestRunner.class)
public class TimestampAdapterUnitTest {
    TimestampAdapter currAdapter;
    private SocialCompassDatabase socialCompassDatabase;

    @Before
    public void init() throws ExecutionException, InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        SocialCompassDatabase.injectTestDatabase(
                Room.inMemoryDatabaseBuilder(context, SocialCompassDatabase.class)
                        .allowMainThreadQueries()
                        .build()
        );

        socialCompassDatabase = SocialCompassDatabase.getInstance(context);
    }

    @After
    public void destroy(){
        socialCompassDatabase.close();
    }

    @Test
    public void TimeStampAdapterWrite() throws IOException {
        currAdapter = new TimestampAdapter();
        ByteArrayOutputStream toConvertToJSON = new ByteArrayOutputStream();

        JsonWriter output =  new JsonWriter(new OutputStreamWriter(toConvertToJSON, "UTF-8"));

        currAdapter.write(output, (long) 1000);

        output.close();

        var instant = Instant.ofEpochSecond((long) 1000);
        assertEquals("\"" + instant.toString() + "\"", toConvertToJSON.toString());
    }

    @Test
    public void TimeStampAdapterRead() throws IOException {
        currAdapter = new TimestampAdapter();
        
        ByteArrayOutputStream toConvertToJSON = new ByteArrayOutputStream();

        JsonWriter output =  new JsonWriter(new OutputStreamWriter(toConvertToJSON, "UTF-8"));

        currAdapter.write(output, (long) 1000);

        output.close();

        ByteArrayInputStream toConvertToString = new ByteArrayInputStream(toConvertToJSON.toByteArray());

        JsonReader reader = new JsonReader(new InputStreamReader(toConvertToString, "UTF-8"));

        long returnedVal = currAdapter.read(reader);

        assertEquals(returnedVal, (long) 1000);
    }
}
