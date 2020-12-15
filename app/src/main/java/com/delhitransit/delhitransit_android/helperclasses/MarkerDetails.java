package com.delhitransit.delhitransit_android.helperclasses;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MarkerDetails {
    private static final String FROM = "From";
    private static final String TO = "To";
    public Integer id;
    public LatLng latLng;
    public String name, relation;
    public StopsResponseData stopsResponseData;
    public Marker marker;

    public MarkerDetails(StopsResponseData stopsResponseData, boolean isSecondSearchView) {
        this.stopsResponseData = stopsResponseData;
        id = stopsResponseData.getStopId();
        name = stopsResponseData.getName();
        latLng = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
        relation = isSecondSearchView ? TO : FROM;
    }

    public void remove() {
        if (marker != null)
            marker.remove();
    }

}
