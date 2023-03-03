package com.cse110.team7.socialcompass.models;

import androidx.annotation.NonNull;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServerHouseAdapter {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("public_code")
    @NonNull
    public String publicID;

    @SerializedName("private_code")
    @NonNull
    @Expose
    public String privateID;

    @SerializedName("label")
    @Expose
    private String name;

    @SerializedName("latitude")
    @Expose
    private double latitude;

    @SerializedName("longitude")
    @Expose
    private double longitude;


    public ServerHouseAdapter(@NonNull String title, @NonNull String content) {
        this.publicID = title;
        this.privateID = content;
        this.name = "";
        this.latitude = -1;
        this.longitude = -1;
    }

    @Ignore
    public ServerHouseAdapter(@NonNull String publicID, @NonNull String privateID, String name,
                              double latitude, double longitude) {
        this.publicID = publicID;
        this.privateID = privateID;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ServerHouseAdapter(House house) {
        this.publicID = house.getPublicID();
        this.privateID = house.getPrivateID();
        this.name = house.getName();
        this.latitude = house.getLocation().getLatitude();
        this.longitude = house.getLocation().getLongitude();
    }

    public House toHouse() {
        return new House(name, new LatLong(latitude, longitude), publicID);
    }

    public static House fromJSON(String json) {
        var hehe = new Gson().fromJson(json, ServerHouseAdapter.class);
        System.err.println(hehe);
                return hehe.toHouse();
    }

    public static List<House> listFromJSON(String json) {
        Type type = new TypeToken<List<ServerHouseAdapter>>() {
        }.getType();
        List<ServerHouseAdapter> serverHouses = new Gson().fromJson(json, type);
        List<House> houseList = new ArrayList<>();
        for (ServerHouseAdapter s : serverHouses) {
            houseList.add(s.toHouse());
        }
        return houseList;
    }

    public String toJSON() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(this);
    }

    public String patchLocationJSON() {
        return new Gson().toJson(Map.of("private_id", this.privateID, "latitude", this.latitude,
                "longitude", this.longitude));
    }

    public String patchRenameJSON() {
        return new Gson().toJson(Map.of("private_id", this.privateID, "label", this.name));
    }

    public String patchPublishJSON(boolean published) {
        return new Gson().toJson(Map.of("private_id", this.privateID, "is_listed_publicly", published));
    }

    public String deleteJSON() {
        return new Gson().toJson(Map.of("private_id", this.privateID));
    }

    public String toString() {
        return "privateID: " + privateID + " publicID: " + publicID + " name " + name;
    }
}
