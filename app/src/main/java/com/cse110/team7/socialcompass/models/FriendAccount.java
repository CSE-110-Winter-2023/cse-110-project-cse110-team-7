package com.cse110.team7.socialcompass.models;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "friend_locations")
public class FriendAccount {

    @PrimaryKey
    public long id;
    @NonNull
    public String publicID;
    public String privateID;
    @NonNull
    private String name;
    private LatLong location;

    //May Need This (Page 7, Lab 6):
    //private int friendNum;

    public FriendAccount(String name, LatLong location) {
        this.name = name;
        this.location = location;
        this.publicID = UUID.randomUUID().toString(); // TODO: check that UID doesn't already exist?
        this.privateID = UUID.randomUUID().toString(); // generate new privateID
        this.id = publicID.hashCode();
    }

    // For populating from remote server
    @Ignore
    public FriendAccount(String name, LatLong location, String publicID) {
        this.name = name;
        this.location = location;
        this.publicID = publicID;
        this.id = publicID.hashCode();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLong getLocation() {
        return location;
    }

    public void setLocation(LatLong location) {
        this.location = location;
    }

    public long getId() { return id; }

    public String getPublicID() {
        return publicID;
    }

    public String getPrivateID() { return privateID; }

    @NonNull
    @Override
    public String toString() {
        return "Friend {" +
                "publicid=" + publicID +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof FriendAccount)) {
            return false;
        }
        FriendAccount h = (FriendAccount) o;
        // do not check private ID because it will be null if pulled from server
        if (!h.getPublicID().equals(getPublicID())) {
            return false;
        }
        if (!h.getName().equals(getName())) {
            return false;
        }
        if (!h.getLocation().equals(getLocation())) {
            return false;
        }
        return true;
    }
}
