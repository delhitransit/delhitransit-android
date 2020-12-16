package com.delhitransit.delhitransit_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

public class RouteStopsAdapter extends ListAdapter<StopDetail, RouteStopsAdapter.RDViewHolder> {

    private static final DiffUtil.ItemCallback<StopDetail> DIFF_CALLBACK = new DiffUtil.ItemCallback<StopDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull StopDetail oldItem, @NonNull StopDetail newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull StopDetail oldItem, @NonNull StopDetail newItem) {
            return oldItem.equals(newItem);
        }
    };

    public RouteStopsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RDViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.route_stops_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RDViewHolder holder, int position) {
        StopDetail stop = getItem(position);
        holder.setText(holder.stopName, stop.getName());
    }

    static class RDViewHolder extends RecyclerView.ViewHolder {

        final TextView stopName;
        final TextView exactTime;
        final TextView exactTimeAMPM;
        final TextView timeCountdown;

        public RDViewHolder(@NonNull View itemView) {
            super(itemView);
            stopName = itemView.findViewById(R.id.route_details_item_stop_name);
            exactTime = itemView.findViewById(R.id.route_details_item_time_numeral);
            exactTimeAMPM = itemView.findViewById(R.id.route_details_item_time_unit);
            timeCountdown = itemView.findViewById(R.id.route_details_item_bus_status);
        }

        public void setText(TextView view, String string) {
            if (string != null && !string.isEmpty()) {
                view.setText(string);
            }
        }

    }

}
