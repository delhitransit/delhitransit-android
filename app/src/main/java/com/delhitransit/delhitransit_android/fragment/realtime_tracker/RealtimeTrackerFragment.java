package com.delhitransit.delhitransit_android.fragment.realtime_tracker;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.helperclasses.GeoLocationHelper;
import com.delhitransit.delhitransit_android.helperclasses.RoutePointsMaker;
import com.delhitransit.delhitransit_android.helperclasses.TrackerStopMarker;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.RealtimeUpdate;
import com.delhitransit.delhitransit_android.pojos.ShapePoint;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.delhitransit.delhitransit_android.helperclasses.ViewMarker.vectorToBitmap;

public class RealtimeTrackerFragment extends Fragment {

    private static final String TAG = RealtimeTrackerFragment.class.getSimpleName();
    private static final int USER_ZOOM_RETENTION_CYCLES = 7;
    private static final float DEFAULT_CAMERA_ZOOM = 17.5f;
    private final HashMap<Marker, StopDetail> stopDetailHashMap = new HashMap<>();
    private LifecycleOwner mLifecycleOwner;
    private String tripId;
    private GoogleMap mMap;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            try {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                RealtimeTrackerFragment.this.requireContext(), R.raw.google_maps_style));
                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
            LatLng latLng = new LatLng(28.6172368, 77.2059964);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));
            mMap.setPadding(100, 600, 100, 100);
        }
    };
    private RealtimeTrackerViewModel mViewModel;
    private Marker busMarker;
    private TextView licensePlateTextView;
    private TextView routeNameTextView;
    private TextView speedTextView;
    private TextView lastUpdatedTextView;
    private Polyline routeCoveredPolyline;
    private Polyline routeRemainingPolyline;
    private MaterialProgressBar horizontalProgressBar;
    private int userZoomRetainIntervalsRemaining = 1;

    @NotNull
    public static String getLastUpdatedString(long timestamp) {
        long updateTimeDelta = (int) (System.currentTimeMillis() / 1000) - timestamp;
        String updateTimeString = "Last updated ";
        if (updateTimeDelta < 60) {
            updateTimeString += updateTimeDelta + " seconds ago";
        } else {
            long minutes = updateTimeDelta / 60;
            if (minutes < 2) {
                updateTimeString += minutes + " minute ago";
            } else if (minutes > 59) {
                long hours = minutes / 60;
                if (hours < 2) {
                    updateTimeString += hours + " hour ago";
                } else {
                    updateTimeString += hours + " hours ago";
                }
            } else updateTimeString += minutes + " minutes ago";
        }
        return updateTimeString;
    }

    public static String getBusSpeedKmph(double speed) {
        return String.format("%d km/h", (int) (speed * 3.6));
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        RealtimeTrackerFragmentArgs args = RealtimeTrackerFragmentArgs.fromBundle(getArguments());
        this.tripId = args.getTripId();
        final View view = inflater.inflate(R.layout.fragment_realtime_tracker, container, false);
        licensePlateTextView = view.findViewById(R.id.realtime_tracker_textView3);
        routeNameTextView = view.findViewById(R.id.realtime_tracker_realtime_route_text);
        speedTextView = view.findViewById(R.id.realtime_tracker_realtime_speed_text);
        lastUpdatedTextView = view.findViewById(R.id.realtime_tracker_realtime_updated_text);
        horizontalProgressBar = view.findViewById(R.id.realtime_tracker_realtime_progress_bar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RealtimeTrackerViewModel.class);
        mLifecycleOwner = getViewLifecycleOwner();
        mViewModel.realtimeUpdateList.observe(mLifecycleOwner, this::onRealtimeUpdate);
        mViewModel.scheduleRealtimeUpdates();
        mViewModel.fetchShapePointsByTripId(this.tripId);
        mViewModel.fetchStopsByTripId(this.tripId);
        setStopsOnMap();
    }

    private void onRealtimeUpdate(List<RealtimeUpdate> updateList) {
        Log.v(TAG, "Response received from server. Size of list : " + updateList.size());
        final Optional<RealtimeUpdate> update = updateList.stream().filter(it -> it.getTripID().equals(tripId)).findAny();
        final boolean isUpdatePresent = update.isPresent();
        Log.v(TAG, "Response filtered. Update present? " + isUpdatePresent + ". Trip Id required: " + this.tripId);
        horizontalProgressBar.setVisibility(View.GONE);
        if (!isUpdatePresent) {
            if (licensePlateTextView.getText().equals(getString(R.string.loading_tracking_information))) {
                licensePlateTextView.setText("Tracking not available");
            }
            return;
        }
        final RealtimeUpdate realtimeUpdate = update.get();
        mViewModel.realtimeUpdate.setValue(realtimeUpdate);
        if (licensePlateTextView.getText().equals(getString(R.string.loading_tracking_information))) {
            licensePlateTextView.setText("Tracking " + realtimeUpdate.getVehicleID());
        }
        if (TextUtils.isEmpty(routeNameTextView.getText())) {
            mViewModel.getRouteByRouteId(realtimeUpdate.getRouteID()).observe(mLifecycleOwner, route -> {
                if (route != null) {
                    routeNameTextView.setText(route.getLongName());
                }
            });
        }
        lastUpdatedTextView.setText(getLastUpdatedString(realtimeUpdate.getTimestamp()));
        speedTextView.setText(getBusSpeedKmph(realtimeUpdate.getSpeed()));
        LatLng latLng = new LatLng(realtimeUpdate.getLatitude(), realtimeUpdate.getLongitude());
        if (busMarker == null) {
            busMarker = mMap.addMarker(new MarkerOptions()
                    .icon(vectorToBitmap(getResources(), R.drawable.bus_icon, Color.parseColor("#FFFFFF"), 0.88f))
                    .position(latLng)
                    .title(realtimeUpdate.getVehicleID()));
        }
        busMarker.setPosition(latLng);
        // Let the user keep their custom zoom level for a few cycles
        float cameraZoom = DEFAULT_CAMERA_ZOOM;
        final float currentCameraZoom = mMap.getCameraPosition().zoom;
        if (currentCameraZoom != DEFAULT_CAMERA_ZOOM) {
            if (--userZoomRetainIntervalsRemaining == 0) {
                this.userZoomRetainIntervalsRemaining = USER_ZOOM_RETENTION_CYCLES;
            } else cameraZoom = currentCameraZoom;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, cameraZoom));
        final List<ShapePoint> routeShapePointList = mViewModel.routeShapePointList;
        final List<StopDetail> routeBusStopsList = mViewModel.routeBusStopsList;
        if (routeBusStopsList.size() > 0) {
            final StopDetail firstStop = routeBusStopsList.get(0);
            final StopDetail lastStop = routeBusStopsList.get(routeBusStopsList.size() - 1);
            final LatLng firstStopLatLng = new LatLng(firstStop.getLatitude(), firstStop.getLongitude());
            final LatLng lastStopLatLng = new LatLng(lastStop.getLatitude(), lastStop.getLongitude());
            final TaskCompleteCallback routeCoveredCallback = (values) -> {
                if (!(values[0] instanceof Boolean)) {
                    if (routeCoveredPolyline != null) {
                        routeCoveredPolyline.remove();
                    }
                    routeCoveredPolyline = mMap.addPolyline((PolylineOptions) values[0]);
                }
            };
            final TaskCompleteCallback routeRemainingCallback = (values) -> {
                if (!(values[0] instanceof Boolean)) {
                    if (routeRemainingPolyline != null) {
                        routeRemainingPolyline.remove();
                    }
                    routeRemainingPolyline = mMap.addPolyline((PolylineOptions) values[0]);
                }
            };
            final LatLng nearestShapePoint = getNearestShapePoint(latLng);
            if (nearestShapePoint != null) {
                new RoutePointsMaker(getResources().getColor(R.color.orange_faded), routeCoveredCallback, firstStopLatLng, nearestShapePoint).execute(routeShapePointList);
                new RoutePointsMaker(Color.parseColor(getString(R.string.orange_dark)), routeRemainingCallback, nearestShapePoint, lastStopLatLng).execute(routeShapePointList);
            }
        }
    }

    private LatLng getNearestShapePoint(LatLng busLocation) {
        ShapePoint nearestShapePoint = null;
        double nearestDistance = Double.MAX_VALUE;
        final GeoLocationHelper busLocationGlh = GeoLocationHelper
                .fromDegrees(busLocation.latitude, busLocation.longitude);
        for (ShapePoint shapePoint : mViewModel.routeShapePointList) {
            if (nearestShapePoint == null) nearestShapePoint = shapePoint;
            final GeoLocationHelper currShapePoint = GeoLocationHelper
                    .fromDegrees(shapePoint.getLatitude(), shapePoint.getLongitude());
            final double distance = busLocationGlh.distanceTo(currShapePoint, null);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestShapePoint = shapePoint;
            }
        }
        if (nearestShapePoint == null) return null;
        return new LatLng(nearestShapePoint.getLatitude(), nearestShapePoint.getLongitude());
    }

    private void setStopsOnMap() {
        mViewModel.allStops.observe(mLifecycleOwner, customizeStopDetails -> {
            if (customizeStopDetails.size() == 0) {
                mViewModel.realtimeUpdate.observe(mLifecycleOwner, realtimeUpdate -> {
                    mViewModel.fetchStopsByRouteId(Integer.parseInt(realtimeUpdate.getRouteID()));
                });
                return;
            }
            for (StopDetail stopDetail : customizeStopDetails) {
                if (stopDetail == null) continue;
                LatLng latLng = new LatLng(stopDetail.getLatitude(), stopDetail.getLongitude());
                final TrackerStopMarker viewMarker = new TrackerStopMarker(getContext(), stopDetail.getName());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(viewMarker.getBitmap())));
                stopDetailHashMap.put(marker, stopDetail);
            }
        });
    }
}