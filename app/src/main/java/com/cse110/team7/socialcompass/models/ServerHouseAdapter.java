package com.cse110.team7.socialcompass.models;

import androidx.annotation.NonNull;
import androidx.room.Ignore;

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
    public ServerHouseAdapter(FriendAccount friendAccount) {
        this.publicID = friendAccount.getPublicID();
        this.privateID = friendAccount.getPrivateID();
        this.name = friendAccount.getName();
        this.latitude = friendAccount.getLocation().getLatitude();
        this.longitude = friendAccount.getLocation().getLongitude();
    }

    public FriendAccount toHouse() {
        return new FriendAccount(name, new LatLong(latitude, longitude), publicID);
    }

    public static FriendAccount fromJSON(String json) {
        var HouseAdapter = new Gson().fromJson(json, ServerHouseAdapter.class);
        return HouseAdapter.toHouse();
    }

    public static List<FriendAccount> listFromJSON(String json) {
        Type type = new TypeToken<List<ServerHouseAdapter>>() {
        }.getType();
        List<ServerHouseAdapter> serverHouses = new Gson().fromJson(json, type);
        List<FriendAccount> friendAccountList = new ArrayList<>();
        for (ServerHouseAdapter s : serverHouses) {
            friendAccountList.add(s.toHouse());
        }
        return friendAccountList;
    }

    public String toJSON() {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.toJson(this);
    }

    public String patchLocationJSON() {
        return new Gson().toJson(Map.of("private_code", this.privateID, "latitude", this.latitude,
                "longitude", this.longitude));
    }

    public String patchRenameJSON() {
        return new Gson().toJson(Map.of("private_code", this.privateID, "label", this.name));
    }

    public String patchPublishJSON() {
        return new Gson().toJson(Map.of("private_code", this.privateID, "is_listed_publicly", true));
    }

    public String deleteJSON() {
        return new Gson().toJson(Map.of("private_code", this.privateID));
    }

    public String toString() {
        return "privateID: " + privateID + "  publicID: " + publicID + "  name: " + name;
    }
}
