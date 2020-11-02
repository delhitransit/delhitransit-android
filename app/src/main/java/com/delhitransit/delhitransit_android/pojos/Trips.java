package com.delhitransit.delhitransit_android.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Trips {
    @SerializedName("tripId")
    @Expose
    private String tripId;
    @SerializedName("key")
    @Expose
    private Integer key;

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }
}