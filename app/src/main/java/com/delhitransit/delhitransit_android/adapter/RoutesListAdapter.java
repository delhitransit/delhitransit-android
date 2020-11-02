package com.delhitransit.delhitransit_android.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.helperclasses.RoutePointsMaker;
import com.delhitransit.delhitransit_android.pojos.Route;
import com.delhitransit.delhitransit_android.pojos.ShapePoint;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutesListAdapter extends RecyclerView.Adapter<RoutesListAdapter.RoutesListViewHolder> {


    Context context;
    private List<Route> list;
    private String destination, source;

    public RoutesListAdapter(Context context, List<Route> list) {
        this.context = context;
        this.list = list;
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
        holder.time3.setText("5:20 PM from xyx place" + position);
        holder.parent.setOnClickListener(v -> {
            ApiClient.getApiService().getAllShapePointsByTripId(route.getTrips().get(0).getTripId()).enqueue(new Callback<List<ShapePoint>>() {
                @Override
                public void onResponse(Call<List<ShapePoint>> call, Response<List<ShapePoint>> response) {
                    if (response.body() != null) {
                        new RoutePointsMaker(context, colorList[3]).execute(response.body());
                    }
                }

                @Override
                public void onFailure(Call<List<ShapePoint>> call, Throwable t) {
                    Log.e("TAG", "onFailure: " + t.getMessage());
                }
            });

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class RoutesListViewHolder extends RecyclerView.ViewHolder {

        TextView busNumber, time1, time2, time3;
        View parent;

        public RoutesListViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            busNumber = itemView.findViewById(R.id.bus_number_text_view);
            time1 = itemView.findViewById(R.id.time_text_view_1);
            time2 = itemView.findViewById(R.id.time_text_view_2);
            time3 = itemView.findViewById(R.id.time_text_view_3);
        }
    }
}
