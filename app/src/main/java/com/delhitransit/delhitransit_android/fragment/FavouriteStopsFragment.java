package com.delhitransit.delhitransit_android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.adapter.FavouriteStopsAdapter;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class FavouriteStopsFragment extends Fragment {

    private FavouriteStopsViewModel mViewModel;
    private FavouriteStopsAdapter adapter;
    private Context context;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.favourite_stops_fragment, container, false);
        recyclerView = parent.findViewById(R.id.fav_stops_recycler_view);
        adapter = new FavouriteStopsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = this.getContext();
        enableSwipe();
        mViewModel = new ViewModelProvider(this).get(FavouriteStopsViewModel.class);
        mViewModel.setFavouriteStopsAdapter(adapter, this);
    }

    private void enableSwipe() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(context) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<StopsResponseData> data = adapter.getCurrentList();
                StopsResponseData element = data.get(position);
                mViewModel.deleteByStopId(element.getStopId());

                // showing snack bar with Undo option
                Snackbar snackbar = Snackbar.make(viewHolder.itemView, "User deleted", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", v -> mViewModel.insertAll(element));
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

}