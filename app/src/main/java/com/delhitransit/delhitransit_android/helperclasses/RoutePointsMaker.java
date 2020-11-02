package com.delhitransit.delhitransit_android.helperclasses;

import android.content.Context;
import android.os.AsyncTask;

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
    private LatLng source, destination;
    private LatLngBounds bounds;


    public RoutePointsMaker(Context context, int color) {
        this.taskCallback = (TaskCompleteCallback) context;
        this.color = color;
    }

    public PolylineOptions makePoly(List<ShapePoint> shapePointList) {

        //Log.e("TAG", "makePoly: called");

        PolylineOptions polylineOptions = new PolylineOptions();
        ArrayList<LatLng> resultPoints = new ArrayList<>();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < shapePointList.size(); i++) {
            LatLng latLng = new LatLng(shapePointList.get(i).getLatitude(), shapePointList.get(i).getLongitude());

            resultPoints.add(latLng);
            builder.include(latLng);

            if (i == 0) {
                source = new LatLng(shapePointList.get(i).getLatitude(), shapePointList.get(i).getLongitude());
            }
            if (i == shapePointList.size() - 1) {
                destination = new LatLng(shapePointList.get(i).getLatitude(), shapePointList.get(i).getLongitude());
            }
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
        taskCallback.onTaskDone(polylineOptions, source, destination, bounds);
    }
}
