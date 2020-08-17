package com.delhitransit.delhitransit_android;

import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.delhitransit.delhitransit_android.interfaces.TaskCompleteCallback;
import com.delhitransit.delhitransit_android.pojos.DataClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskCompleteCallback {

    private GoogleMap mMap;
    private MarkerOptions place1, place2;
    private Polyline currentPolyline;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private CardView searchCardView;
    private SearchView busStopSearchView_1, busStopSearchView_2;
    private int statusBarHeight;
    private boolean isKeyboardOn = false;
    private LinearLayout tryLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        busStopSearchView_1 = findViewById(R.id.bus_stop_search_view_1);
        busStopSearchView_2 = findViewById(R.id.bus_stop_search_view_2);
        searchCardView = findViewById(R.id.search_card_view_1);
        tryLayout = findViewById(R.id.try_layout_for_start);

        busStopSearchView_1.setIconified(false);
        busStopSearchView_1.clearFocus();
        busStopSearchView_1.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MapsActivity.this, "onClose", Toast.LENGTH_SHORT).show();
                shiftDown(searchCardView);
                return true;
            }
        });
        busStopSearchView_1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                shiftDown(searchCardView);
                isKeyboardOn = false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!isKeyboardOn) {
                    shiftUp(searchCardView);
                }
                isKeyboardOn = true;
                return true;
            }
        });
        /*busStopSearchView_2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                shiftDown(searchCardView);
                isKeyboardOn = false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (isKeyboardOn) {
                    shiftUp(searchCardView);
                }
                return false;
            }
        });*/
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            findViewById(R.id.status_bar_bg).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, statusBarHeight));
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

/*
        setWindowFlag( WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);*/
        /*busStopSearchEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(TAG, "beforeTextChanged: " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e(TAG, "onTextChanged: " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "afterTextChanged: " + s);
            }
        });
        busStopSearchEditView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                shiftDown(searchCardView);
                return true;
            }
            return false;
        });
*/

        //place1 = new MarkerOptions().position(new LatLng(22.3039, 70.8022)).title("Location 1");
        // place2 = new MarkerOptions().position(new LatLng(23.0225, 72.5714)).title("Location 2");
    }


    public void shiftUp(View view) {
        view.animate().setDuration(200).translationY((float) (1.8 * (view.getTop()) - statusBarHeight)).start();
        int cx = tryLayout.getMeasuredWidth() / 2;
        int cy = 0;

        int finalRadius = Math.max(tryLayout.getWidth(), tryLayout.getHeight()) / 2;
        Animator anim = ViewAnimationUtils.createCircularReveal(tryLayout, cx, cy, 0, finalRadius).setDuration(500);
        tryLayout.setVisibility(View.VISIBLE);
        anim.start();
    }

    public void shiftDown(View view) {
        view.animate().setDuration(200).translationY(0).start();
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

        Log.e(TAG, "onMapReady: call");
        //new DataClass(MapsActivity.this).execute(DataClass.data2);

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        /*mMap.addMarker(place1);
        mMap.addMarker(place2);
        new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(new LatLng(22.7739, 71.6673))
                .zoom(7)
                .bearing(0)
                .tilt(45)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);*/
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
    /*private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }*/
}