package com.delhitransit.delhitransit_android.helperclasses;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

public class CircleMarker {

    Marker start, end;

    public CircleMarker(GoogleMap mMap, Context context, Polyline currentPolyline) {
        final ViewMarker viewMarker = new ViewMarker(context);
        start = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(ViewMarker.getBitmap(viewMarker.view))).anchor(0.5f, 0.5f).position(currentPolyline.getPoints().get(0)));
        start.setZIndex(2);
        final ViewMarker viewMarker1 = new ViewMarker(context);
        end = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(ViewMarker.getBitmap(viewMarker1.view))).anchor(0.5f, 0.5f).position(currentPolyline.getPoints().get(currentPolyline.getPoints().size() - 1)));
        end.setZIndex(2);
    }

    public void remove() {
        if (start != null && end != null) {
            start.remove();
            end.remove();
        }
    }
}
