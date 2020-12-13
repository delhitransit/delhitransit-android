package com.delhitransit.delhitransit_android.pojos.stops;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "favourite_stop")
public class StopsResponseData {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long key = -1L;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("stopId")
    @Expose
    private Integer stopId;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Long getKey() {
        return key;
    }

    public void setKey(@NotNull Long key) {
        this.key = key;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStopId() {
        return stopId;
    }

    public void setStopId(Integer stopId) {
        this.stopId = stopId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof StopsResponseData)) return super.equals(obj);
        else {
            StopsResponseData otherStop = (StopsResponseData) obj;
            return Objects.equals(otherStop.getLatitude(), this.getLatitude()) &&
                    Objects.equals(otherStop.getLongitude(), this.getLongitude()) &&
                    Objects.equals(otherStop.getName(), this.getName()) &&
                    Objects.equals(otherStop.getStopId(), this.getStopId());
        }
    }
}