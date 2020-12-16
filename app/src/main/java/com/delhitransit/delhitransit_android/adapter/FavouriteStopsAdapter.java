package com.delhitransit.delhitransit_android.adapter;

import android.annotation.SuppressLint;
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

public class FavouriteStopsAdapter extends ListAdapter<StopDetail, FavouriteStopsAdapter.FSViewHolder> {

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

    public FavouriteStopsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public FSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FSViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_stop_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FSViewHolder holder, int position) {
        StopDetail stop = getItem(position);
        holder.setStopName(stop.getName());
        holder.setStopId(stop.getStopId());
    }

    static class FSViewHolder extends RecyclerView.ViewHolder {

        private final TextView stopName;
        private final TextView stopId;

        public FSViewHolder(@NonNull View itemView) {
            super(itemView);
            stopName = itemView.findViewById(R.id.fav_stop_item_stop_name);
            stopId = itemView.findViewById(R.id.fav_stop_item_stop_id);
        }

        public void setStopName(String name) {
            if (stopName != null) {
                stopName.setText(name);
            }
        }

        @SuppressLint("SetTextI18n")
        public void setStopId(Integer id) {
            if (stopId != null && id != null) {
                stopId.setText(id.toString());
            }
        }
    }

}
