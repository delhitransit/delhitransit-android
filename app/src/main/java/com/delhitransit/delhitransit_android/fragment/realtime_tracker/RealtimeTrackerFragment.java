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
import com.delhitransit.delhitransit_android.helperclasses.TimeConverter;
import com.delhitransit.delhitransit_android.helperclasses.TrackerStopMarker;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.RealtimeUpdate;
import com.delhitransit.delhitransit_android.pojos.ShapePoint;
import com.delhitransit.delhitransit_android.pojos.stops.CustomizeStopDetail;
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

import static com.delhitransit.delhitransit_android.helperclasses.TimeConverter.getSecondsSince12AM;
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
    private View bottomSheet;
    private TextView nextStopTitle;
    private View nextStopCardView;
    private TextView nextStopNameTextView;
    private TextView nextStopTimeNumeral;
    private TextView nextStopTimeUnit;
    private TextView nextStopStatusTextView;
    private View scheduledStopLinearLayout;
    private View nextStopLinearLayout;
    private TextView scheduledStopTitle;
    private View scheduledStopCardView;
    private TextView scheduledStopNameTextView;
    private TextView scheduledStopTimeNumeral;
    private TextView scheduledStopTimeUnit;
    private TextView scheduledStopStatusTextView;

    @NotNull
    public static String getLastUpdatedString(long timestamp) {
        long updateTimeDelta = (int) (System.currentTimeMillis() / 1000) - timestamp;
        String updateTimeString = "Last updated ";
        updateTimeString += secondsToTimeString(updateTimeDelta);
        updateTimeString += " ago";
        return updateTimeString;
    }

    @NotNull
    public static String secondsToTimeString(long timeInSeconds) {
        String result = "";
        if (timeInSeconds < 60) {
            result += timeInSeconds + " seconds";
        } else {
            long minutes = timeInSeconds / 60;
            if (minutes < 2) {
                result += minutes + " minute";
            } else if (minutes > 59) {
                long hours = minutes / 60;
                if (hours < 2) {
                    result += hours + " hour";
                } else {
                    result += hours + " hours";
                }
            } else result += minutes + " minutes";
        }
        return result;
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
        bottomSheet = view.findViewById(R.id.realtime_tracker_bottom_sheet);
        nextStopLinearLayout = view.findViewById(R.id.realtime_tracker_next_stop_layout);
        nextStopTitle = view.findViewById(R.id.realtime_tracker_next_stop_title);
        nextStopCardView = view.findViewById(R.id.realtime_tracker_next_stop_card_view);
        nextStopNameTextView = view.findViewById(R.id.realtime_tracker_next_stop_stop_name);
        nextStopTimeNumeral = view.findViewById(R.id.realtime_tracker_next_stop_time_numeral);
        nextStopTimeUnit = view.findViewById(R.id.realtime_tracker_next_stop_time_unit);
        nextStopStatusTextView = view.findViewById(R.id.route_details_item_bus_status);
        scheduledStopLinearLayout = view.findViewById(R.id.realtime_tracker_scheduled_stop_layout);
        scheduledStopTitle = view.findViewById(R.id.realtime_tracker_scheduled_stop_title);
        scheduledStopCardView = view.findViewById(R.id.realtime_tracker_scheduled_stop_card_view);
        scheduledStopNameTextView = view.findViewById(R.id.realtime_tracker_scheduled_stop_stop_name);
        scheduledStopTimeNumeral = view.findViewById(R.id.realtime_tracker_scheduled_stop_time_numeral);
        scheduledStopTimeUnit = view.findViewById(R.id.realtime_tracker_scheduled_stop_time_unit);
        scheduledStopStatusTextView = view.findViewById(R.id.realtime_tracker_scheduled_bus_status);
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
                licensePlateTextView.setText(R.string.realtime_tracker_no_data_text);
            }
            return;
        }
        final RealtimeUpdate realtimeUpdate = update.get();
        mViewModel.realtimeUpdate.setValue(realtimeUpdate);
        if (licensePlateTextView.getText().equals(getString(R.string.loading_tracking_information)))
            licensePlateTextView.setText(String.format("Tracking %s", realtimeUpdate.getVehicleID()));
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
                new RoutePointsMaker(getResources().getColor(R.color.orange_dark), routeCoveredCallback, firstStopLatLng, nearestShapePoint).execute(routeShapePointList);
                new RoutePointsMaker(getResources().getColor(R.color.orange_faded), routeRemainingCallback, nearestShapePoint, lastStopLatLng).execute(routeShapePointList);
            }
        }
        updateBottomStopsDatasheet(realtimeUpdate);
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
        mViewModel.allStops.observe(mLifecycleOwner, stopDetails -> {
            if (stopDetails.size() == 0) {
                mViewModel.realtimeUpdate.observe(mLifecycleOwner, realtimeUpdate -> mViewModel.fetchStopsByRouteId(Integer.parseInt(realtimeUpdate.getRouteID())));
                return;
            }
            for (StopDetail stopDetail : stopDetails) {
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

    private void updateBottomStopsDatasheet(RealtimeUpdate realtimeUpdate) {
        LatLng currBusLocation = new LatLng(realtimeUpdate.getLatitude(), realtimeUpdate.getLongitude());
        mViewModel.customStopsOfTrip.observe(mLifecycleOwner, customizeStopDetails -> {
            //list of customised stops that contains expected arrival time
            if (customizeStopDetails.size() != 0) {
                //Log.e(TAG, "setStopsData: "+customizeStopDetails.get(0).getName()+customizeStopDetails.get(0).getArrivalTime() );
                //getting bus stop which is near by realtime bus.
                CustomizeStopDetail nearestStop = null;
                CustomizeStopDetail scheduledStop = null;
                long currTime = getSecondsSince12AM();
                long minTime = Long.MAX_VALUE;
                double nearestDistance = Double.MAX_VALUE;
                final GeoLocationHelper busLocationGlh = GeoLocationHelper.fromDegrees(currBusLocation.latitude, currBusLocation.longitude);
                for (CustomizeStopDetail customizeStopDetail : customizeStopDetails) {
                    if (nearestStop == null) nearestStop = customizeStopDetail;
                    final GeoLocationHelper currStop = GeoLocationHelper
                            .fromDegrees(customizeStopDetail.getLatitude(), customizeStopDetail.getLongitude());
                    final double distance = busLocationGlh.distanceTo(currStop, null);
                    if (distance < nearestDistance) {
                        nearestDistance = distance;
                        nearestStop = customizeStopDetail;
                    }
                    long diff = currTime - customizeStopDetail.getArrivalTime();
                    if (Math.abs(diff) < minTime) {
                        minTime = diff;
                        scheduledStop = customizeStopDetail;
                    }
                }
                if (scheduledStop != null) {
                    Log.e(TAG, "setStopsData: scheduledStop: " + scheduledStop.getName() + scheduledStop.getArrivalTime());
                }
                //calculate estimate arrival time using realtime data of bus
                //time = distance/speed
                if (realtimeUpdate.getSpeed() != 0) {
                    double estimatedTime = (nearestDistance / realtimeUpdate.getSpeed()) * 60 * 60;
                    Log.e(TAG, "setStopsData: estimated time " + estimatedTime + " distance: " + nearestDistance);
                }
                //Now waiting to test this code/

                if (nearestStop != null) {
                    renderNextStopCard(nearestStop);
                } else Log.e(TAG, "Nearest stop to the current bus location could not be found.");

                if (scheduledStop != null && !nearestStop.equals(scheduledStop)) {
                    renderScheduledStopCard(scheduledStop, nearestStop, customizeStopDetails);
                }
            }
        });
    }

    private void renderNextStopCard(CustomizeStopDetail nextStop) {
        bottomSheet.setVisibility(View.VISIBLE);
        nextStopLinearLayout.setVisibility(View.VISIBLE);
        nextStopNameTextView.setText(nextStop.getName());
        TimeConverter arrivalTimeConverter = new TimeConverter((long) nextStop.getArrivalTime());
        nextStopTimeNumeral.setText(arrivalTimeConverter.justHrAndMin);
        nextStopTimeUnit.setText(arrivalTimeConverter.state);
        long timeDelta = nextStop.getArrivalTime() - getSecondsSince12AM();
        String nextStopStatusString;
        if (Math.abs(timeDelta) > 300) {
            if (timeDelta > 0) {
                nextStopStatusString = "Arriving " + secondsToTimeString(Math.abs(timeDelta)) + " earlier than scheduled";
            } else {
                nextStopStatusString = "Running late by " + secondsToTimeString(Math.abs(timeDelta));
            }
        } else nextStopStatusString = "The bus is running on time";
        nextStopStatusTextView.setText(nextStopStatusString);
    }

    private void renderScheduledStopCard(CustomizeStopDetail scheduledStop, CustomizeStopDetail actualCurrentStop, List<CustomizeStopDetail> stopDetails) {
        bottomSheet.setVisibility(View.VISIBLE);
        scheduledStopLinearLayout.setVisibility(View.VISIBLE);
        scheduledStopNameTextView.setText(scheduledStop.getName());

        int scheduledStopIndex = stopDetails.indexOf(scheduledStop);
        int actualCurrStopIndex = stopDetails.indexOf(actualCurrentStop);

        int earlierStopListIndex = Math.min(actualCurrStopIndex, scheduledStopIndex);
        int laterStopListIndex = Math.max(actualCurrStopIndex, scheduledStopIndex);

        int travelTime = 0;
        int prevArrivalTime = stopDetails.get(earlierStopListIndex).getArrivalTime();

        for (int i = earlierStopListIndex + 1; i <= laterStopListIndex; i++) {
            int currArrivalTime = stopDetails.get(i).getArrivalTime();
            travelTime += currArrivalTime - prevArrivalTime;
            prevArrivalTime = currArrivalTime;
        }

        if (actualCurrStopIndex > scheduledStopIndex) travelTime = Math.abs(travelTime) * (-1);

        int calibratedArrivalTime = scheduledStop.getArrivalTime() + travelTime;
        int calibratedArrivalTimeSecondsSince12AM = (int) getSecondsSince12AM() - calibratedArrivalTime;

        TimeConverter calibratedArrivalTimeConverter = new TimeConverter((long) calibratedArrivalTime);
        scheduledStopTimeNumeral.setText(calibratedArrivalTimeConverter.justHrAndMin);
        scheduledStopTimeUnit.setText(calibratedArrivalTimeConverter.state);

        TimeConverter arrivalTimeConverter = new TimeConverter((long) scheduledStop.getArrivalTime());
        String busStatusString = busStatusByArrivalTimeDeltaString(calibratedArrivalTimeSecondsSince12AM);
        busStatusString += "\nScheduled arrival time: " + arrivalTimeConverter.justHrAndMinWithState;
        scheduledStopStatusTextView.setText(busStatusString);
    }

    @NotNull
    private String busStatusByArrivalTimeDeltaString(int timeDelta) {
        String result;
        if (timeDelta > 0) {
            result = "The bus arrived " + secondsToTimeString(Math.abs(timeDelta)) + " early";
        } else result = "The bus is running " + secondsToTimeString(Math.abs(timeDelta)) + " late";
        return result;
    }
}