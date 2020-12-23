package com.delhitransit.delhitransit_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.route_stops.RouteStopsFragmentDirections;
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.pojos.stops.CustomizeStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;

public class RouteStopsAdapter extends ListAdapter<CustomizeStopDetail, RouteStopsAdapter.RDViewHolder> {

    private static final DiffUtil.ItemCallback<CustomizeStopDetail> DIFF_CALLBACK = new DiffUtil.ItemCallback<CustomizeStopDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull CustomizeStopDetail oldItem, @NonNull CustomizeStopDetail newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull CustomizeStopDetail oldItem, @NonNull CustomizeStopDetail newItem) {
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
        CustomizeStopDetail stop = getItem(position);
        holder.setText(holder.stopName, stop.getName());
        long arrivalTime = stop.getArrivalTime();
        TimeConverter timeConverter = new TimeConverter(arrivalTime);
        holder.setText(holder.exactTime, timeConverter.justHrAndMin);
        holder.setText(holder.exactTimeAMPM, timeConverter.state);

        long timeDelta = arrivalTime - TimeConverter.getSecondsSince12AM();
        TimeConverter countdownTimer = new TimeConverter(timeDelta);
        StringBuilder builder = new StringBuilder();
        if (timeDelta == 0) builder.append("Now");
        else if (timeDelta < 0) {
            builder.append("Departed");
            // TODO FIX does'nt work
            holder.parentView.setAlpha(0.75f);
        } else builder.append("Scheduled");
        String timeDeltaString = "";
        if (countdownTimer.hourOfDay > 0) {
            timeDeltaString = Long.toString(countdownTimer.hourOfDay);
            timeDeltaString += " ";
            timeDeltaString += countdownTimer.hourOfDay > 1 ? "hours" : "hour";
        } else if (countdownTimer.min > 1) {
            timeDeltaString = Long.toString(countdownTimer.min);
            timeDeltaString += " ";
            timeDeltaString += countdownTimer.min > 1 ? "mins" : "min";
        }
        builder.append(" ");
        builder.append(timeDeltaString);
        holder.setText(holder.timeCountdown, builder.toString());

        holder.parentView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            StopDetail stopDetail = stop.getStopDetail();
            RouteStopsFragmentDirections.ActionRouteStopsFragmentToStopDetailsFragment action = RouteStopsFragmentDirections.actionRouteStopsFragmentToStopDetailsFragment(stopDetail);
            navController.navigate(action);
        });

    }

    static class RDViewHolder extends RecyclerView.ViewHolder {

        final TextView stopName;
        final TextView exactTime;
        final TextView exactTimeAMPM;
        final TextView timeCountdown;
        final View parentView;

        public RDViewHolder(@NonNull View itemView) {
            super(itemView);
            parentView = itemView;
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
