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
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.google.android.material.chip.Chip;

import java.util.function.Consumer;

public class StopDetailsAdapter extends ListAdapter<RoutesFromStopDetail, StopDetailsAdapter.SDViewHolder> {

    private static final DiffUtil.ItemCallback<RoutesFromStopDetail> DIFF_CALLBACK = new DiffUtil.ItemCallback<RoutesFromStopDetail>() {
        @Override
        public boolean areItemsTheSame(@NonNull RoutesFromStopDetail oldItem, @NonNull RoutesFromStopDetail newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull RoutesFromStopDetail oldItem, @NonNull RoutesFromStopDetail newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final Consumer<RoutesFromStopDetail> mConsumer;

    public StopDetailsAdapter(Consumer<RoutesFromStopDetail> consumer) {
        super(DIFF_CALLBACK);
        this.mConsumer = consumer;
    }

    @NonNull
    @Override
    public SDViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SDViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_details_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SDViewHolder holder, int position) {
        RoutesFromStopDetail item = getItem(position);
        holder.setText(holder.routeName, item.getRouteLongName());
        holder.setText(holder.lastStopName, item.getLastStopName());
        int earliestTime = item.getEarliestTime();
        holder.setText(holder.exactTime, new TimeConverter(((long) earliestTime)).justHrAndMinWithState);
        TimeConverter countdownTimer = new TimeConverter(earliestTime - TimeConverter.getSecondsSince12AM());
        if (countdownTimer.hourOfDay > 0) {
            holder.setText(holder.timeNumeral, Long.toString(countdownTimer.hourOfDay));
            holder.setText(holder.timeUnit, countdownTimer.hourOfDay > 1 ? "hours" : "hour");
        } else {
            if (countdownTimer.min < 1) {
                holder.setText(holder.timeNumeral, "Now");
                holder.timeUnit.setVisibility(View.GONE);
            } else {
                holder.setText(holder.timeNumeral, Long.toString(countdownTimer.min));
                holder.setText(holder.timeUnit, countdownTimer.min > 1 ? "mins" : "min");
            }
        }
        holder.parentView.setOnClickListener((s) -> {
            mConsumer.accept(item);
        });
    }

    static class SDViewHolder extends RecyclerView.ViewHolder {

        final View parentView;
        final TextView lastStopName;
        final Chip routeName;
        final TextView exactTime;
        final TextView timeNumeral;
        final TextView timeUnit;

        public SDViewHolder(@NonNull View itemView) {
            super(itemView);
            parentView = itemView;
            lastStopName = itemView.findViewById(R.id.stop_details_item_last_stop_name);
            routeName = itemView.findViewById(R.id.stop_details_item_route_name);
            exactTime = itemView.findViewById(R.id.stop_details_item_exact_time);
            timeNumeral = itemView.findViewById(R.id.stop_details_item_time_numeral);
            timeUnit = itemView.findViewById(R.id.stop_details_item_time_unit);
        }

        public void setText(View view, String string) {
            if (string != null && !string.isEmpty() && view instanceof TextView) {
                ((TextView) view).setText(string);
            }
        }

    }

}
