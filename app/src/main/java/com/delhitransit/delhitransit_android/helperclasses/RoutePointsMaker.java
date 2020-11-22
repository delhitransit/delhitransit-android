package com.delhitransit.delhitransit_android.helperclasses;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.ShapePoint;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class RoutePointsMaker extends AsyncTask<List<ShapePoint>, Integer, PolylineOptions> {
    private final int color;
    TaskCompleteCallback taskCallback;
    LatLng source, destination;
    private LatLngBounds bounds;


    public RoutePointsMaker(Context context, int color, LatLng source, LatLng destination) {
        this.color = color;
        this.taskCallback = (TaskCompleteCallback) context;
        this.source = source;
        this.destination = destination;
    }

    public PolylineOptions makePoly(List<ShapePoint> shapePointList) {

        //Log.e("TAG", "makePoly: called");

        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<LatLng> resultPoints = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        int sourcePosition = 0, destinationPosition = shapePointList.size() - 1;
        Location sourceLocation = new Location("locationS");
        sourceLocation.setLatitude(source.latitude);
        sourceLocation.setLongitude(source.longitude);

        Location destinationLocation = new Location("locationD");
        destinationLocation.setLatitude(destination.latitude);
        destinationLocation.setLongitude(destination.longitude);

        double minFromSource = Double.MAX_VALUE, minFromDestination = Double.MAX_VALUE;

        Log.e("TAG", " Size-" + shapePointList.size() + "-");
        for (int i = 0; i < shapePointList.size(); i++) {

            Location startPoint = new Location("Temp");
            startPoint.setLatitude(shapePointList.get(i).getLatitude());
            startPoint.setLongitude(shapePointList.get(i).getLongitude());

            double temp = startPoint.distanceTo(sourceLocation);
            if (minFromSource > temp) {
                //Log.e("TAG", "temp PS-" + minFromSource + "m "+"NS -" + temp+" i=" + i);
                minFromSource = temp;
                sourcePosition = i;

            }
            temp = startPoint.distanceTo(destinationLocation);
            if (minFromDestination > temp) {
                // Log.e("TAG", "temp PD-" + minFromDestination + "m "+"ND -" + temp+" i=" + i);
                minFromDestination = temp;
                destinationPosition = i;

            }


        }
        //Log.e("TAG", "makePoly: S-" + sourcePosition + "-    D-" + destinationPosition);
        if (sourcePosition == destinationPosition) {
            Log.e("TAG", "Not plot");
        }
        for (int i = sourcePosition; i <= destinationPosition; i++) {
            LatLng latLng = new LatLng(shapePointList.get(i).getLatitude(), shapePointList.get(i).getLongitude());
            resultPoints.add(latLng);
            builder.include(latLng);
        }

        bounds = builder.build();
        polylineOptions.addAll(resultPoints);
        polylineOptions.width(20);
        polylineOptions.color(color);
        return polylineOptions;
    }

    @Override
    protected PolylineOptions doInBackground(List<ShapePoint>... lists) {
        return makePoly(lists[0]);
    }

    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        //Log.e("TAG", "onPostExecute: called");
        taskCallback.onTaskDone(polylineOptions, bounds);
    }
}
