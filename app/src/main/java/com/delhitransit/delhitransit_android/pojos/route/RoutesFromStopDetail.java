package com.delhitransit.delhitransit_android.pojos.route;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Objects;

public class RoutesFromStopDetail implements Serializable {
    @Expose
    private int earliestTime;
    @Expose
    private String lastStopName;
    @Expose
    private int routeId;
    @Expose
    private String routeLongName;
    @Expose
    private String tripId;

    public int getEarliestTime() {
        return earliestTime;
    }

    public void setEarliestTime(int earliestTime) {
        this.earliestTime = earliestTime;
    }

    public String getLastStopName() {
        return lastStopName;
    }

    public void setLastStopName(String lastStopName) {
        this.lastStopName = lastStopName;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public void setRouteLongName(String routeLongName) {
        this.routeLongName = routeLongName;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof RoutesFromStopDetail)) return super.equals(obj);
        else {
            RoutesFromStopDetail otherItem = (RoutesFromStopDetail) obj;
            return Objects.equals(otherItem.getEarliestTime(), this.getEarliestTime()) &&
                    Objects.equals(otherItem.getLastStopName(), this.getLastStopName()) &&
                    Objects.equals(otherItem.getRouteId(), this.getRouteId()) &&
                    Objects.equals(otherItem.getRouteLongName(), this.getRouteLongName()) &&
                    Objects.equals(otherItem.getTripId(), this.getTripId());
        }
    }
}
