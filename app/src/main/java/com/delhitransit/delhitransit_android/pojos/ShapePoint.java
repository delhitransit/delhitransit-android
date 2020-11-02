package com.delhitransit.delhitransit_android.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ShapePoint {
    @SerializedName("sequence")
    @Expose
    private Integer sequence;
    @SerializedName("shapeId")
    @Expose
    private Integer shapeId;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("key")
    @Expose
    private Integer key;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getShapeId() {
        return shapeId;
    }

    public void setShapeId(Integer shapeId) {
        this.shapeId = shapeId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}