package com.delhitransit.delhitransit_android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.helperclasses.RoutePointsMaker;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.interfaces.OnRouteSelectedListener;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.Route;
import com.delhitransit.delhitransit_android.pojos.ShapePoint;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutesListAdapter extends RecyclerView.Adapter<RoutesListAdapter.RoutesListViewHolder> {


    Context context;
    LatLng source, destination;
    private List<Route> list;
    private OnRouteSelectedListener onRouteSelectedListener;
    private String sourceBusStopName;

    public RoutesListAdapter(Context context, List<Route> list, OnRouteSelectedListener onRouteSelectedListener) {
        this.context = context;
        this.list = list;
        this.onRouteSelectedListener = onRouteSelectedListener;
    }

    @NonNull
    @Override
    public RoutesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoutesListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.routes_detail_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoutesListViewHolder holder, int position) {
        Route route = list.get(position);

        int[] colorList = {Color.RED, Color.BLACK, Color.CYAN, Color.YELLOW, Color.BLUE, Color.GRAY};

        holder.busNumber.setText(route.getLongName());
        setTimeInLayout(holder.bothTime, holder.sourceTime, sourceBusStopName, getSecondsSince12AM(), getTravelTime(), holder.travelTimeHr, holder.hrDisplay, holder.travelTimeMin, holder.minDisplay);
        holder.parent.setOnClickListener(v -> {
            onRouteSelectedListener.OnRouteSelected();
            ApiClient.getApiService().getAllShapePointsByTripId(route.getTrips().get(0).getTripId()).enqueue(new Callback<List<ShapePoint>>() {
                @Override
                public void onResponse(Call<List<ShapePoint>> call, Response<List<ShapePoint>> response) {
                    if (response.body() != null && response.body().size() != 0) {
                        new RoutePointsMaker(context, colorList[4], source, destination).execute(response.body());
                    } else {
                        ((TaskCompleteCallback) context).onTaskDone(false);
                    }
                }

                @Override
                public void onFailure(Call<List<ShapePoint>> call, Throwable t) {
                    Log.e("TAG", "onFailure: " + t.getMessage());
                    ((TaskCompleteCallback) context).onTaskDone(false);
                }
            });

        });

    }

    private void setTimeInLayout(TextView bothTime, TextView sourceTime, String sourceBusStopName, long arrivalTime, long travelTime, TextView travelTimeHr, TextView hrDisplay, TextView travelTimeMin, TextView minDisplay) {

        if (sourceBusStopName != null) {
            sourceTime.setText((new TimeConverter(arrivalTime).justHrAndMinWithState + " from " + sourceBusStopName)); //+ position);
        } else {
            sourceTime.setVisibility(View.GONE);
        }
        bothTime.setText((new TimeConverter(arrivalTime).justHrAndMin + " - " + new TimeConverter(arrivalTime + travelTime).justHrAndMinWithState));
        TimeConverter converter = new TimeConverter(travelTime);
        if (converter.hr != 0) {
            travelTimeHr.setVisibility(View.VISIBLE);
            travelTimeHr.setText(("" + converter.hr));
            hrDisplay.setVisibility(View.VISIBLE);
        } else {
            travelTimeHr.setVisibility(View.GONE);
            hrDisplay.setVisibility(View.GONE);
        }
        if (converter.min == 0 && converter.hr == 0) {
            minDisplay.setVisibility(View.GONE);
            travelTimeMin.setText(("NOW"));
        } else {
            minDisplay.setVisibility(View.VISIBLE);
            travelTimeMin.setText(("" + converter.min));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setSourceAndDestination(LatLng source, LatLng destination) {
        this.source = source;
        this.destination = destination;
    }

    public void setSourceBusStopName(String sourceBusStopName) {
        this.sourceBusStopName = sourceBusStopName;
    }

    public long getSecondsSince12AM() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        return now.get(Calendar.HOUR_OF_DAY) * 3600 + (now.get(Calendar.MINUTE) + (int) (Math.random() * 20)) * 60 + now.get(Calendar.SECOND);

    }

    public long getTravelTime() {
        return (int) (Math.random() * 80) * 60 + (int) (Math.random() * 60);

    }

    static class RoutesListViewHolder extends RecyclerView.ViewHolder {

        TextView busNumber, bothTime, sourceTime, travelTimeHr, travelTimeMin, hrDisplay, minDisplay;
        View parent;

        public RoutesListViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            busNumber = itemView.findViewById(R.id.bus_number_text_view);
            bothTime = itemView.findViewById(R.id.time_text_view_2);
            sourceTime = itemView.findViewById(R.id.time_text_view_3);
            travelTimeHr = itemView.findViewById(R.id.hr_text_view);
            travelTimeMin = itemView.findViewById(R.id.min_text_view);
            hrDisplay = itemView.findViewById(R.id.fix_hr_text_view);
            minDisplay = itemView.findViewById(R.id.fix_min_text_view);
        }
    }
}
