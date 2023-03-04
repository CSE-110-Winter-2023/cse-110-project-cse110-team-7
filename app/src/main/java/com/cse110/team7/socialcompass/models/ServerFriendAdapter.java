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

public class ServerFriendAdapter {
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


    public ServerFriendAdapter(@NonNull String title, @NonNull String content) {
        this.publicID = title;
        this.privateID = content;
        this.name = "";
        this.latitude = -1;
        this.longitude = -1;
    }

    @Ignore
    public ServerFriendAdapter(FriendAccount friendAccount) {
        this.publicID = friendAccount.getPublicID();
        this.privateID = friendAccount.getPrivateID();
        this.name = friendAccount.getName();
        this.latitude = friendAccount.getLocation().getLatitude();
        this.longitude = friendAccount.getLocation().getLongitude();
    }

    public FriendAccount toFriend() {
        return new FriendAccount(name, new LatLong(latitude, longitude), publicID);
    }

    public static FriendAccount fromJSON(String json) {
        var FriendAdapter = new Gson().fromJson(json, ServerFriendAdapter.class);
        return FriendAdapter.toFriend();
    }

    public static List<FriendAccount> listFromJSON(String json) {
        Type type = new TypeToken<List<ServerFriendAdapter>>() {
        }.getType();
        List<ServerFriendAdapter> serverFriends = new Gson().fromJson(json, type);
        List<FriendAccount> friendAccountList = new ArrayList<>();
        for (ServerFriendAdapter s : serverFriends) {
            friendAccountList.add(s.toFriend());
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
