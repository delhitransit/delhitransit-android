package com.delhitransit.delhitransit_android.pojos.route;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {
    @SerializedName("routeId")
    @Expose
    private Integer routeId;

    @SerializedName("shortName")
    @Expose
    private String shortName;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("longName")
    @Expose
    private String longName;

    public Route(Integer routeId, String shortName, String type, String longName) {
        this.routeId = routeId;
        this.shortName = shortName;
        this.type = type;
        this.longName = longName;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public String getShortName() {
        return shortName;
    }

    public String getType() {
        return type;
    }

    public String getLongName() {
        return longName;
    }
}