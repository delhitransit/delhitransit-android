package com.delhitransit.delhitransit_android.interfaces;

import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

public interface OnStopMarkerClickedListener {
    void onStopMarkerClick(StopDetail stop, Runnable fabClickCallback);
}