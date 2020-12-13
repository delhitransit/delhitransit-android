package com.delhitransit.delhitransit_android.fragment;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;

import java.util.List;

public class FavouriteStopsAdapter extends RecyclerView.Adapter<FavouriteStopsAdapter.FSViewHolder> {

    private final Context context;
    private final List<StopsResponseData> allFavouriteStops;

    public FavouriteStopsAdapter(List<StopsResponseData> allFavouriteStops, Context context) {
        this.allFavouriteStops = allFavouriteStops;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return allFavouriteStops.size();
    }

    @NonNull
    @Override
    public FSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FSViewHolder(((Activity) context).getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FSViewHolder holder, int position) {
        StopsResponseData stop = allFavouriteStops.get(position);
        holder.setStopName(stop.getName());
    }

    static class FSViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public FSViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }

        public void setStopName(String name) {
            if (textView != null) {
                textView.setText(name);
            }
        }
    }

}
