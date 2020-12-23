package com.delhitransit.delhitransit_android.interfaces;

import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

public interface OnRouteDetailsSelectedListener {
    void onRouteSelect(RoutesFromStopDetail routeDetail, StopDetail stopDetail);
}
