package com.delhitransit.delhitransit_android;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.LocationManager;

public class DelhiTransitApplication extends Application {

    private static final String SERVER_IP_KEY = "serverIpAddress";
    private static final String SERVER_IP_KEY_M = "serverIpAddressM";
    private static final String SERVER_IP_TOGGLE = "manualIp";
    private static final String LOCATION_PROVIDER_KEY = "locationProvider";
    private static final String DESTINATION_STOPS_FILTERED_KEY = "filtered_destination_stops";
    private static final String UPDATE_TIME_KEY = "realtime_update_time";
    private SharedPreferences sharedPreferences;
    private String DEFAULT_SERVER_IP;
    private String DEFAULT_LOCATION_PROVIDER;
    private String DEFAULT_UPDATE_TIME;

    @Override
    public void onCreate() {
        super.onCreate();
        DEFAULT_SERVER_IP = getString(R.string.default_server_ip);
        DEFAULT_LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
        DEFAULT_UPDATE_TIME = "5";
        sharedPreferences = getSharedPreferences("com.delhitransit.delhitransit_android_preferences", MODE_PRIVATE);

        if (sharedPreferences.getBoolean(SERVER_IP_TOGGLE, false)) {
            if (sharedPreferences.contains(SERVER_IP_KEY_M)) {
                String manualServerIP = sharedPreferences.getString(SERVER_IP_KEY_M, "");
                if (!manualServerIP.equals("")) {
                    setServerIP(manualServerIP);
                }
            }
        } else {
            if (!sharedPreferences.contains(SERVER_IP_KEY) || sharedPreferences.getString(SERVER_IP_KEY, "").equals("")
                    || sharedPreferences.getString(SERVER_IP_KEY, "").equals(sharedPreferences.getString(SERVER_IP_KEY_M, ""))) {
                setServerIP(DEFAULT_SERVER_IP);
            }
        }

        if (!sharedPreferences.contains(LOCATION_PROVIDER_KEY)) {
            setLocationProvider(DEFAULT_LOCATION_PROVIDER);
        }

        if (!sharedPreferences.contains(UPDATE_TIME_KEY)) {
            setUpdateTime(DEFAULT_UPDATE_TIME);
        }
    }

    public String getServerIP() {
        return sharedPreferences.getString(SERVER_IP_KEY, DEFAULT_SERVER_IP);
    }

    public void setServerIP(String ip) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVER_IP_KEY, ip);
        editor.apply();
    }

    public String getLocationProvider() {
        return sharedPreferences.getString(LOCATION_PROVIDER_KEY, DEFAULT_LOCATION_PROVIDER);
    }

    private void setLocationProvider(String provider) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOCATION_PROVIDER_KEY, provider);
        editor.apply();
    }

    public boolean isDestinationStopsFiltered() {
        return sharedPreferences.getBoolean(DESTINATION_STOPS_FILTERED_KEY, false);
    }

    public String getUpdateTime() {
        return sharedPreferences.getString(UPDATE_TIME_KEY, DEFAULT_UPDATE_TIME);
    }

    public void setUpdateTime(String timeInSec) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UPDATE_TIME_KEY, timeInSec.trim());
        editor.apply();
    }
}
