package com.cse110.team7.socialcompass.models;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "compass_houses")
public class House {

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String publicID;
    public String privateID;
    private String name;
    private LatLong location;

    //May Need This (Page 7, Lab 6):
    //private int houseNum;

    public House(String name, LatLong location) {
        this.name = name;
        this.location = location;
        this.publicID = name.replace(" ", "-"); // TODO: assign UIDs so that they are unique
        this.privateID = UUID.randomUUID().toString(); // generate new privateID
    }

    // For populating from remote server
    @Ignore
    public House(String name, LatLong location, String publicID) {
        this.name = name;
        this.location = location;
        this.publicID = publicID;
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
        return "House{" +
                "id=" + publicID +
                ", name='" + name + '\'' +
                ", location=" + location +
                '}';
    }
}
