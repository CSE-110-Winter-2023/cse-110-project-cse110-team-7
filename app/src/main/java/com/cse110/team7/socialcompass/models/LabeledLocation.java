package com.cse110.team7.socialcompass.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.cse110.team7.socialcompass.utils.TimestampAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a user, which is just a labeled location on server
 */
@Entity(tableName = "labeled_locations")
public class LabeledLocation {
    /**
     * the unique public code of the user
     */
    @NonNull
    @PrimaryKey
    @SerializedName("public_code")
    private String publicCode;
    /**
     * the private code of the user
     */
    @SerializedName("private_code")
    private String privateCode;
    /**
     * the label of the location, or the name of the user
     */
    @SerializedName("label")
    private String label;
    /**
     * the current latitude of the location
     */
    @SerializedName("latitude")
    private double latitude;
    /**
     * the current longitude of the location
     */
    @SerializedName("longitude")
    private double longitude;
    /**
     * whether the labeled location is listed publicly on the server
     * this can only be modified by patching the server
     */
    @SerializedName("is_listed_publicly")
    private boolean isListedPublicly;
    /**
     * the timestamp representing when the labeled location is created in seconds
     */
    @SerializedName("created_at")
    @JsonAdapter(TimestampAdapter.class)
    private long createdAt;
    /**
     * the timestamp representing when the labeled location is last updated in seconds
     */
    @SerializedName("updated_at")
    @JsonAdapter(TimestampAdapter.class)
    private long updatedAt;

    public LabeledLocation(
            @NonNull String publicCode,
            String privateCode,
            String label,
            double latitude,
            double longitude,
            boolean isListedPublicly,
            long createdAt,
            long updatedAt
    ) {
        this.publicCode = publicCode;
        this.privateCode = privateCode;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isListedPublicly = isListedPublicly;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Get the current coordinate of the labeled location
     * @return the current location as a coordinate on map
     */
    @NonNull
    public Coordinate getCoordinate() {
        return new Coordinate(latitude, longitude);
    }

    /**
     * Set the current coordinate of the labeled location
     * @param coordinate the current coordinate
     */
    public void setCoordinate(@NonNull Coordinate coordinate) {
        this.latitude = coordinate.latitude;
        this.longitude = coordinate.longitude;
    }

    private LabeledLocation(Builder builder) {
        this.publicCode = builder.publicCode;
        this.privateCode = builder.privateCode;
        this.label = builder.label;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.isListedPublicly = builder.isListedPublicly;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    /**
     * Builder for a labeled location
     */
    public static class Builder {
        private String publicCode;
        private String privateCode;
        private String label;
        private double latitude;
        private double longitude;
        private boolean isListedPublicly;
        private long createdAt;
        private long updatedAt;

        public Builder() {
            this.publicCode = UUID.randomUUID().toString();
            this.privateCode = UUID.randomUUID().toString();
            this.label = "";
            this.latitude = 0;
            this.longitude = 0;
            this.isListedPublicly = false;
            this.createdAt = Instant.now().getEpochSecond();
            this.updatedAt = Instant.now().getEpochSecond();
        }

        public Builder setPublicCode(String publicCode) {
            this.publicCode = publicCode;
            return this;
        }

        public Builder setPrivateCode(String privateCode) {
            this.privateCode = privateCode;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setListedPublicly(boolean listedPublicly) {
            isListedPublicly = listedPublicly;
            return this;
        }

        public Builder setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(long updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public LabeledLocation build() {
            return new LabeledLocation(this);
        }
    }

    @NonNull
    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(@NonNull String publicCode) {
        this.publicCode = publicCode;
    }

    public String getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(String privateCode) {
        this.privateCode = privateCode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isListedPublicly() {
        return isListedPublicly;
    }

    public void setListedPublicly(boolean listedPublicly) {
        isListedPublicly = listedPublicly;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "LabeledLocation{" +
                "publicCode='" + publicCode + '\'' +
                ", privateCode='" + privateCode + '\'' +
                ", label='" + label + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isListedPublicly=" + isListedPublicly +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
