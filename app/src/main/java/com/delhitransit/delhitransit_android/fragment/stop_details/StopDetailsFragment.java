package com.delhitransit.delhitransit_android.fragment.stop_details;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.adapter.StopDetailsAdapter;
import com.delhitransit.delhitransit_android.interfaces.FragmentFinisherInterface;
import com.delhitransit.delhitransit_android.interfaces.OnRouteDetailsSelectedListener;
import com.delhitransit.delhitransit_android.pojos.route.RoutesFromStopDetail;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.function.Consumer;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

// TODO implement callback passing
public class StopDetailsFragment extends Fragment {

    public static final String KEY_FRAGMENT_BACKSTACK = StopDetailsFragment.class.getSimpleName() + System.currentTimeMillis();
    private StopDetail stop;
    //    private final Runnable fabClickCallback;
    private boolean mFavourite = false;
    private StopDetailsViewModel mViewModel;
    private StopDetailsAdapter adapter;
    private MaterialToolbar toolbar;
    private MaterialProgressBar horizontalProgressBar;
    private Activity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.stop_details_fragment, container, false);
        this.stop = StopDetailsFragmentArgs.fromBundle(getArguments()).getStopDetail();
        activity = getActivity();
        //Setup the toolbar
        toolbar = parent.findViewById(R.id.stop_details_fragment_app_bar);
        toolbar.setTitle(stop.getName());
        toolbar.setNavigationOnClickListener(item -> finishMe(null));
        //Setup the recycler view
        RecyclerView recyclerView = parent.findViewById(R.id.stop_details_fragment_recycler_view);
        horizontalProgressBar = parent.findViewById(R.id.horizontal_loading_bar);
        Consumer<RoutesFromStopDetail> consumer = routesFromStopDetail -> {
            if (activity instanceof OnRouteDetailsSelectedListener) {
                ((OnRouteDetailsSelectedListener) activity).onRouteSelect(routesFromStopDetail, stop);
            } else {
                Log.e(StopDetailsFragment.this.getClass().getSimpleName(), "Calling class does not implement " + OnRouteDetailsSelectedListener.class.getSimpleName());
            }
        };
        adapter = new StopDetailsAdapter(consumer);
        recyclerView.setAdapter(adapter);
        recyclerView.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        //Setup the navigate FAB
        ExtendedFloatingActionButton fab = parent.findViewById(R.id.extended_navigate_fab);
        fab.setOnClickListener(item -> {
//            finishMe(fabClickCallback);
        });
        //Collapse or expand the FAB on scrolling the nestedScrollView
        NestedScrollView nestedScrollView = parent.findViewById(R.id.stop_details_fragment_scrollable_content);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            // the delay of the extension of the FAB is set for 12 items
            int MODE_TOGGLE_THRESHOLD = 12;
            if (scrollY > oldScrollY + MODE_TOGGLE_THRESHOLD && fab.isExtended()) {
                fab.shrink();
            }
            if (scrollY < oldScrollY - MODE_TOGGLE_THRESHOLD && !fab.isExtended()) {
                fab.extend();
            }
            if (scrollY == 0) {
                fab.extend();
            }
        });
        return parent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StopDetailsViewModel.class);
        if (stop == null) return;
        int stopId = stop.getStopId();
        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();
        mViewModel.getAllRoutes(stopId).observe(lifecycleOwner, list -> {
            adapter.submitList(list);
            horizontalProgressBar.setVisibility(View.GONE);
        });
        final int favouriteStopMenuButtonId = R.id.favourite_stop_menu_button;
        mViewModel.isStopFavourite(stopId).observe(lifecycleOwner, value -> {
            mFavourite = value;
            setMenuItemState(toolbar.getMenu().findItem(favouriteStopMenuButtonId), mFavourite);
        });
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == favouriteStopMenuButtonId) {
                mFavourite = !mFavourite;
                setMenuItemState(item, mFavourite);
                if (mFavourite) {
                    mViewModel.addToFavourites(stop);
                } else mViewModel.removeFromFavourites(stopId);
                return true;
            }
            return false;
        });
    }

    private void setMenuItemState(MenuItem item, boolean favourited) {
        if (favourited) {
            item.setTitle("Unfavourite");
            item.setIcon(R.drawable.ic_baseline_star_24);
        } else {
            item.setTitle("Favourite");
            item.setIcon(R.drawable.ic_baseline_star_outline_24);
        }
    }

    public void finishMe(Runnable callback) {
        if (activity instanceof FragmentFinisherInterface) {
            ((FragmentFinisherInterface) activity).finishAndExecute(KEY_FRAGMENT_BACKSTACK, callback == null ? () -> {
            } : callback);
        }
    }

}