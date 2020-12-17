package com.delhitransit.delhitransit_android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.MapsFragmentDirections;
import com.delhitransit.delhitransit_android.interfaces.OnStopMarkerClickedListener;
import com.delhitransit.delhitransit_android.pojos.stops.StopDetail;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

public class AppActivity extends AppCompatActivity implements OnStopMarkerClickedListener {

    private static final int WINDOW_DECORATION_FLAG = FLAG_TRANSLUCENT_STATUS;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(bottomNav, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            Window window = AppActivity.this.getWindow();
            if (destination.getId() == R.id.mapsFragment) {
                window.addFlags(WINDOW_DECORATION_FLAG);
            } else {
                window.clearFlags(WINDOW_DECORATION_FLAG);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(() -> {
            if (!isNetworkConnected()) {
                runOnUiThread(() -> new MaterialAlertDialogBuilder(this)
                        .setTitle("Device offline")
                        .setMessage("Please turn on your mobile data or connect to a WiFi network")
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Turn on WiFi", (dialog, which) -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))).show());
            } else if (!isServerReachable()) {
                runOnUiThread(() -> new MaterialAlertDialogBuilder(this)
                        .setTitle("Server unreachable")
                        .setMessage("Cannot connect to remote server. You can change the server IP or try again in a little while")
                        .setNegativeButton("Dismiss", (dialog, which) -> dialog.dismiss())
                        .setPositiveButton("Change IP address", (dialog, which) -> findViewById(R.id.settingsFragment).performClick()).show());
            }
        }).start();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isServerReachable() {
        try {
            URL serverIP = new URL(((DelhiTransitApplication) getApplication()).getServerIP());
            Socket socket = new Socket();
            int port = serverIP.getPort();
            socket.connect(new InetSocketAddress(serverIP.getHost(), port == -1 ? 80 : port), 5000);
            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onStopMarkerClick(StopDetail stop, Runnable fabClickCallback) {
        if (stop == null) return;
        MapsFragmentDirections.ActionMapsFragmentToStopDetailsFragment action = MapsFragmentDirections.actionMapsFragmentToStopDetailsFragment(stop);
        navController.navigate(action);
    }

}