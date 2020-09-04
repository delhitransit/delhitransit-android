package com.delhitransit.delhitransit_android;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.delhitransit.delhitransit_android.api.ApiClient;
import com.delhitransit.delhitransit_android.api.ApiInterface;
import com.delhitransit.delhitransit_android.helperclasses.BusStopsSuggestion;
import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.DataClass;
import com.delhitransit.delhitransit_android.pojos.stops.StopsResponseData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.FragmentActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskCompleteCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private final int LOCATION_ON_REQUEST_CODE = 101;

    FusedLocationProviderClient mFusedLocationClient;
    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private CardView searchCardView;
    private SearchView busStopSearchView_1, busStopSearchView_2;
    private int statusBarHeight;
    private boolean isKeyboardOn = false;
    private LinearLayout tryLayout;
    private SimpleCursorAdapter searchViewAdapter_1;
    private ApiInterface apiService;
    private ArrayList<StopsResponseData> listOfCityName = new ArrayList<>();
    private FloatingSearchView floatingBusStopSearchView_1;
    private SpinKitView progressBar;
    private double userLatitude, userLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        apiService = ApiClient.getApiService();
        setStatusBar();

        floatingBusStopSearchView_1 = findViewById(R.id.floating_bus_stop_search_view_1);
        progressBar = findViewById(R.id.progress_bar);

        floatingBusStopSearchView_1.setVisibility(View.GONE);
        floatingBusStopSearchView_1.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                if (newQuery.equals("")) {
                    floatingBusStopSearchView_1.clearSuggestions();
                } else if (!newQuery.trim().equals("")) {
                    floatingBusStopSearchView_1.showProgress();
                    apiService.getStopsByName(newQuery, false).enqueue(new Callback<List<StopsResponseData>>() {
                        @Override
                        public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                            if (response.body() != null) {
                                List<BusStopsSuggestion> busStopsSuggestions = new ArrayList<>();
                                for (StopsResponseData stopsResponseData : response.body()) {
                                    busStopsSuggestions.add(new BusStopsSuggestion(stopsResponseData));
                                }
                                floatingBusStopSearchView_1.swapSuggestions(busStopsSuggestions);
                            }
                            floatingBusStopSearchView_1.hideProgress();
                        }

                        @Override
                        public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                            Log.e(TAG, "onFailure: int " + t.getMessage());
                            floatingBusStopSearchView_1.hideProgress();
                        }
                    });

                }
            }
        });
        floatingBusStopSearchView_1.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                BusStopsSuggestion suggestion = (BusStopsSuggestion) searchSuggestion;
                setBusStopMarker(suggestion.getStopsResponseData());
            }

            @Override
            public void onSearchAction(String currentQuery) {
                apiService.getStopsByName(currentQuery, true).enqueue(new Callback<List<StopsResponseData>>() {
                    @Override
                    public void onResponse(Call<List<StopsResponseData>> call, Response<List<StopsResponseData>> response) {
                        if (response.body() != null) {
                            if (response.body().size() != 0) {
                                setBusStopMarker(response.body().get(0));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<StopsResponseData>> call, Throwable t) {
                        Log.e(TAG, "onFailure: int " + t.getMessage());
                    }
                });

            }
        });
 }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_ON_REQUEST_CODE) {
            getUserLocation();
        }
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getUserLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkPermissions()) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (isLocationEnabled(locationManager)) {
                try {
                    progressBarVisibility(true);
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            userLatitude = location.getLatitude();
                            userLongitude = location.getLongitude();
                            progressBarVisibility(false);
                            setUserLocation(true);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }

                    }, null);
                } catch (SecurityException e) {
                    Log.e(TAG, "getLastLocation: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(findViewById(R.id.map), "Please turn on your location", Snackbar.LENGTH_INDEFINITE)
                        .setAction("TURN ON", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, LOCATION_ON_REQUEST_CODE);
                            }
                        })
                        .show();
            }

        } else {
            requestPermissions();
        }
    }

    private void progressBarVisibility(boolean visible) {
        progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setUserLocation(boolean isZoom) {
        LatLng latLng = new LatLng(userLatitude, userLongitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        if (isZoom) mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }

    private boolean isLocationEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }


    private void setBusStopMarker(StopsResponseData stopsResponseData) {
        LatLng latLng = new LatLng(stopsResponseData.getLatitude(), stopsResponseData.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        setUserLocation(false);
        floatingBusStopSearchView_1.clearSearchFocus();
    }

    private void setStatusBar() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            findViewById(R.id.status_bar_bg).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }


    public void shiftUp(View view) {
        //view.animate().setDuration(200).translationY((float) (1.8 * (view.getTop()) - statusBarHeight)).start();
        int cx = tryLayout.getMeasuredWidth() / 2;
        int cy = 0;

        if (busStopSearchView_1.isAttachedToWindow()) {
            int finalRadius = Math.max(tryLayout.getWidth(), tryLayout.getHeight()) / 2;
            Animator anim = ViewAnimationUtils.createCircularReveal(tryLayout, cx, cy, 0, finalRadius).setDuration(500);
            tryLayout.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    public void shiftDown(View view) {
        //view.animate().setDuration(200).translationY(0).start();
        int cx = tryLayout.getMeasuredWidth() / 2;
        int cy = tryLayout.getMeasuredWidth();

        int initialRadius = tryLayout.getWidth() / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(tryLayout, cx, cy, initialRadius, 0).setDuration(500);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                tryLayout.setVisibility(View.INVISIBLE);
            }
        });
        anim.start();
    }

    @Override
    public void onBackPressed() {
        if (isKeyboardOn) {
            shiftDown(searchCardView);
            isKeyboardOn = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        progressBarVisibility(false);
        floatingBusStopSearchView_1.setVisibility(View.VISIBLE);

        LatLng latLng = new LatLng(28.6172368, 77.2059964);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        getUserLocation();

         }


    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
        mMap.addMarker(new MarkerOptions().position(new LatLng(((DataClass.Points) values[1]).lat, ((DataClass.Points) values[1]).lon)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(((DataClass.Points) values[2]).lat, ((DataClass.Points) values[2]).lon)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(((DataClass.Points) values[1]).lat, ((DataClass.Points) values[1]).lon), 15));
    }


}