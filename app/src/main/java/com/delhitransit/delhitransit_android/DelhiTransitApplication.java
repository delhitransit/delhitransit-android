package com.delhitransit.delhitransit_android;

import android.app.Application;
import android.content.SharedPreferences;

public class DelhiTransitApplication extends Application {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(DelhiTransitApplication.class.getSimpleName(), MODE_PRIVATE);
    }

    private static final String SERVER_IP_KEY = "serverIpAddress";

    public String getServerIP() {
        return sharedPreferences.getString(SERVER_IP_KEY, getString(R.string.default_server_ip));
    }

    public void setServerIP(String ip) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SERVER_IP_KEY, ip);
        editor.apply();
    }

}
