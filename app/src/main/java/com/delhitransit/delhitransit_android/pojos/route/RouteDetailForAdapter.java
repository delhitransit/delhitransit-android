package com.delhitransit.delhitransit_android.pojos.route;

public class RouteDetailForAdapter {

    private Integer travelTime;
    private Integer routeId;
    private String tripId;
    private Long busTimings;
    private String longName;

    public RouteDetailForAdapter(Integer travelTime, Integer routeId, String tripId, Long busTimings, String longName) {
        this.travelTime = travelTime;
        this.routeId = routeId;
        this.tripId = tripId;
        this.busTimings = busTimings;
        this.longName = longName;
    }

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

    public Long getBusTimings() {
        return busTimings;
    }

    public void setBusTimings(Long busTimings) {
        this.busTimings = busTimings;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }
}
