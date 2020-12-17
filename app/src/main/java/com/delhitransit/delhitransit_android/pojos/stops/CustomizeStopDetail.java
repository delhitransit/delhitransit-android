package com.delhitransit.delhitransit_android.pojos.stops;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class CustomizeStopDetail extends StopDetail {

    @SerializedName("stopName")
    @Expose
    private String stopName;
    @Expose
    private String tripId;
    @Expose
    private int arrivalTime;

    public StopDetail getStopDetail() {
        super.setName(this.stopName);
        return this;
    }

    @Override
    public String getName() {
        return stopName;
    }

    @Override
    public void setName(String stopName) {
        this.stopName = stopName;
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
                    Objects.equals(otherStop.getStopId(), this.getStopId()) &&
                    super.equals(otherStop);
        }
    }
}
