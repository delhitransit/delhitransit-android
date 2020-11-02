package com.delhitransit.delhitransit_android.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Route {
    @SerializedName("routeId")
    @Expose
    private Integer routeId;
    @SerializedName("trips")
    @Expose
    private List<Trips> trips;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("key")
    @Expose
    private Integer key;
    @SerializedName("longName")
    @Expose
    private String longName;

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public List<Trips> getTrips() {
        return trips;
    }

    public void setTrips(List<Trips> trips) {
        this.trips = trips;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}