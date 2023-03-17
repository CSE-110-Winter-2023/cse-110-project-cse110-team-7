package com.cse110.team7.socialcompass;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.cse110.team7.socialcompass.utils.LabeledLocationDeserializer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * Test conversion to JSON and deserialization
 *
 * **** Needs to be worked on.
 */
@RunWith(RobolectricTestRunner.class)
public class LabeledLocationDeserializerUnitTest {
//    private ObjectMapper mapper;
    private LabeledLocationDeserializer deserializer;

    @Before
    public void init() {
//        mapper = new ObjectMapper();
        deserializer = new LabeledLocationDeserializer();
    }

    @Test
    public void testDeserialize() {
//        var random = TestContext.CurrentContext.Random;

        LabeledLocation.Builder toBuild = new LabeledLocation.Builder();
        toBuild.setLabel("Test");
        toBuild.setCreatedAt(0);
        toBuild.setLatitude(0);
        toBuild.setLongitude(0);
        toBuild.setPublicCode("Test Public Code");
        toBuild.setPrivateCode("Test Private Code");
        toBuild.setUpdatedAt(0);

        LabeledLocation loc = toBuild.build();


        Gson g = new Gson();

        var keep = g.toJson(loc);

        assertEquals("{\"public_code\":\"Test Public Code\"," +
                "\"private_code\":\"Test Private Code\"," +
                "\"label\":\"Test\"," +
                "\"latitude\":0.0," +
                "\"longitude\":0.0," +
                "\"is_listed_publicly\":false," +
                "\"created_at\":\"1970-01-01T00:00:00Z\"," +
                "\"updated_at\":\"1970-01-01T00:00:00Z\"}",
                keep);
    }
}

