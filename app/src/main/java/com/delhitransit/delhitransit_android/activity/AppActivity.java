package com.delhitransit.delhitransit_android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.R;
import com.delhitransit.delhitransit_android.fragment.MapsFragment;
import com.delhitransit.delhitransit_android.fragment.SettingsFragment;
import com.delhitransit.delhitransit_android.fragment.favourite_stops.FavouriteStopsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class AppActivity extends AppCompatActivity {

    public static final short MAPS_FRAGMENT = 0;
    public static final short SETTINGS_FRAGMENT = 1;
    public static final short FAVOURITE_STOPS_FRAGMENT = 2;
    private static final String INTENT_FRAGMENT_KEY = "fragmentKey";
    private final HashMap<Short, Fragment> fragmentMap = new HashMap<>();
    private short currentFragment = MAPS_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        currentFragment = getIntent().getShortExtra(INTENT_FRAGMENT_KEY, MAPS_FRAGMENT);
        navigateTo(currentFragment);

        switch (currentFragment) {
            case MAPS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.map_tab_button);
                break;
            case SETTINGS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.settings_tab_button);
                break;
            case FAVOURITE_STOPS_FRAGMENT:
                bottomNav.setSelectedItemId(R.id.fav_stops_tab_button);
                break;
        }

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.map_tab_button) {
                navigateTo(MAPS_FRAGMENT);
            } else if (itemId == R.id.settings_tab_button) {
                navigateTo(SETTINGS_FRAGMENT);
            } else if (itemId == R.id.fav_stops_tab_button) {
                navigateTo(FAVOURITE_STOPS_FRAGMENT);
            }
            return true;
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
                        .setPositiveButton("Change IP address", (dialog, which) -> findViewById(R.id.settings_tab_button).performClick()).show());
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

    private void navigateTo(short fragmentId) {
        FragmentManager manager = getSupportFragmentManager();
        List<Fragment> managerFragments = manager.getFragments();
        FragmentTransaction transaction = manager.beginTransaction();

        short previousFragment = currentFragment;

        Fragment prevFragment = fragmentMap.get(previousFragment);

        if (prevFragment != null && managerFragments.contains(prevFragment)) {
            prevFragment.onPause();
            transaction.hide(prevFragment);
        }

        switch (fragmentId) {
            default:
            case MAPS_FRAGMENT: {
                showOrAddFragmentTransaction(MAPS_FRAGMENT, new MapsFragment(), managerFragments, transaction);
                break;
            }
            case SETTINGS_FRAGMENT: {
                showOrAddFragmentTransaction(SETTINGS_FRAGMENT, new SettingsFragment(), managerFragments, transaction);
                break;
            }
            case FAVOURITE_STOPS_FRAGMENT: {
                showOrAddFragmentTransaction(FAVOURITE_STOPS_FRAGMENT, new FavouriteStopsFragment(), managerFragments, transaction);
                break;
            }
        }
        transaction.commit();
    }

    private void showOrAddFragmentTransaction(short fragmentId, Fragment defaultFragment, List<Fragment> managerFragments, FragmentTransaction transaction) {
        Fragment currentFragment = fragmentMap.getOrDefault(fragmentId, defaultFragment);
        fragmentMap.put(fragmentId, currentFragment);
        this.currentFragment = fragmentId;
        if (!managerFragments.contains(currentFragment)) {
            transaction.add(R.id.fragment_container, currentFragment);
        } else {
            transaction.show(currentFragment);
            currentFragment.onResume();
        }
    }

}