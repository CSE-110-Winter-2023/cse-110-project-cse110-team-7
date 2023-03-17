package com.cse110.team7.socialcompass.utils;

import androidx.annotation.VisibleForTesting;

import com.cse110.team7.socialcompass.models.LabeledLocation;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;


/**
 * Customize deserialization for LabeledLocation, convert invalid json into null
 */
public class LabeledLocationDeserializer implements JsonDeserializer<LabeledLocation> {
    private static final Gson GSON = new Gson();

    @Override
    public LabeledLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var jsonObject = json.getAsJsonObject();

        if (!jsonObject.has("public_code")) return null;

        return GSON.fromJson(json, new TypeToken<LabeledLocation>() {}.getType());
    }
}
