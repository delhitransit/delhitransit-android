package com.delhitransit.delhitransit_android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.helperclasses.RoutePointsMaker;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.interfaces.OnRouteSelectedListener;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.ShapePoint;
import com.delhitransit.delhitransit_android.pojos.route.RouteDetailForAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutesListAdapter extends RecyclerView.Adapter<RoutesListAdapter.RoutesListViewHolder> {


    private final List<RouteDetailForAdapter> list;
    private final OnRouteSelectedListener onRouteSelectedListener;
    private final TaskCompleteCallback taskCompleteCallback;
    Context context;
    LatLng source, destination;
    private String sourceBusStopName;

    public RoutesListAdapter(Context context, List<RouteDetailForAdapter> list, OnRouteSelectedListener onRouteSelectedListener, TaskCompleteCallback taskCompleteCallback) {
        this.context = context;
        this.list = list;
        this.onRouteSelectedListener = onRouteSelectedListener;
        this.taskCompleteCallback = taskCompleteCallback;
    }

    @NonNull
    @Override
    public RoutesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RoutesListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.routes_detail_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoutesListViewHolder holder, int position) {
        RouteDetailForAdapter routeDetail = list.get(position);

        int[] colorList = {Color.RED, Color.BLACK, Color.CYAN, Color.YELLOW, Color.BLUE, Color.GRAY};
        if (routeDetail.getRouteId() >= 533) {
            holder.busStopIcon.setImageResource(R.drawable.bus_icon);
        } else {
            holder.busStopIcon.setImageResource(R.drawable.ic_outline_directions_bus_24);
        }
        holder.busNumber.setText(routeDetail.getLongName());
        setTimeInLayout(holder.bothTime, holder.sourceTime, sourceBusStopName, routeDetail.getBusTimings(), routeDetail.getTravelTime(), holder.travelTimeHr, holder.hrDisplay, holder.travelTimeMin, holder.minDisplay);
        holder.parent.setOnClickListener(v -> {
            onRouteSelectedListener.OnRouteSelected();
            ApiClient.getApiService(context).getAllShapePointsByTripId(routeDetail.getTripId()).enqueue(new Callback<List<ShapePoint>>() {
                @Override
                public void onResponse(Call<List<ShapePoint>> call, Response<List<ShapePoint>> response) {
                    if (response.body() != null && response.body().size() != 0) {
                        new RoutePointsMaker(colorList[4], taskCompleteCallback, source, destination).execute(response.body());
                    } else {
                        taskCompleteCallback.onTaskDone(false);
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
            sourceTime.setText((new TimeConverter(arrivalTime - travelTime).justHrAndMinWithState + " from " + sourceBusStopName)); //+ position);
        } else {
            sourceTime.setVisibility(View.GONE);
        }
        bothTime.setText((new TimeConverter(arrivalTime - travelTime).justHrAndMin + " - " + new TimeConverter(arrivalTime).justHrAndMinWithState));
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

    static class RoutesListViewHolder extends RecyclerView.ViewHolder {

        TextView busNumber, bothTime, sourceTime, travelTimeHr, travelTimeMin, hrDisplay, minDisplay;
        ImageView busStopIcon;
        View parent;

        public RoutesListViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            busNumber = itemView.findViewById(R.id.bus_number_text_view);
            busStopIcon = itemView.findViewById(R.id.bus_icon_image_view);
            bothTime = itemView.findViewById(R.id.time_text_view_2);
            sourceTime = itemView.findViewById(R.id.time_text_view_3);
            travelTimeHr = itemView.findViewById(R.id.hr_text_view);
            travelTimeMin = itemView.findViewById(R.id.min_text_view);
            hrDisplay = itemView.findViewById(R.id.fix_hr_text_view);
            minDisplay = itemView.findViewById(R.id.fix_min_text_view);
        }
    }
}
