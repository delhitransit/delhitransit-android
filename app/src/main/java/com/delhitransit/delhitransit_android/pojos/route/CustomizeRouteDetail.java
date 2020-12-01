package com.delhitransit.delhitransit_android.pojos.route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomizeRouteDetail {
    @SerializedName("travelTime")
    @Expose
    private Integer travelTime;
    @SerializedName("routeId")
    @Expose
    private Integer routeId;
    @SerializedName("tripId")
    @Expose
    private String tripId;
    @SerializedName("busTimings")
    @Expose
    private List<String> busTimings;
    @SerializedName("longName")
    @Expose
    private String longName;

    public Integer getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(Integer travelTime) {
        this.travelTime = travelTime;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public List<String> getBusTimings() {
        return busTimings;
    }

    public void setBusTimings(List<String> busTimings) {
        this.busTimings = busTimings;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}