package com.delhitransit.delhitransit_android.pojos.stops;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CustomizeStopDetail {

    @Expose
    private Double latitude;
    @SerializedName("stopName")
    @Expose
    private String name;
    @Expose
    private int stopId;
    @Expose
    private Double longitude;
    @Expose
    private String tripId;
    @Expose
    private int arrivalTime;

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

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CustomizeStopDetail)) return super.equals(obj);
        else {
            CustomizeStopDetail otherStop = (CustomizeStopDetail) obj;
            return Objects.equals(otherStop.getLatitude(), this.getLatitude()) &&
                    Objects.equals(otherStop.getLongitude(), this.getLongitude()) &&
                    Objects.equals(otherStop.getName(), this.getName()) &&
                    Objects.equals(otherStop.getStopId(), this.getStopId());
        }
    }
}
