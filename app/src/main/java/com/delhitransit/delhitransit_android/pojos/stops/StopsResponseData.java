package com.delhitransit.delhitransit_android.pojos.stops;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopsResponseData {
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

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStopId(Integer stopId) {
        this.stopId = stopId;
    }

    public Integer getStopId() {
        return stopId;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}