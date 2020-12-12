package com.delhitransit.delhitransit_android;

import android.app.Application;
import android.content.SharedPreferences;

public class DelhiTransitApplication extends Application {

    private static final String SERVER_IP_KEY = "serverIpAddress";
    private SharedPreferences sharedPreferences;
    private String DEFAULT_SERVER_IP;

    @Override
    public void onCreate() {
        super.onCreate();
        DEFAULT_SERVER_IP = getString(R.string.default_server_ip);
        sharedPreferences = getSharedPreferences("com.delhitransit.delhitransit_android_preferences", MODE_PRIVATE);
        if (!sharedPreferences.contains(SERVER_IP_KEY)) {
            setServerIP(DEFAULT_SERVER_IP);
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

}
