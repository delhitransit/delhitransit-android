package com.delhitransit.delhitransit_android.api;

import android.content.Context;
import android.widget.Toast;

import com.delhitransit.delhitransit_android.DelhiTransitApplication;
import com.delhitransit.delhitransit_android.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //private static final String BASE_URL = "http://delhitransit.herokuapp.com/";
    //private static final String BASE_URL = "http://www.delhitransit.ml/";

    private static Retrofit retrofit = null;

    private static Retrofit getApiClient(Context context) {
        String serverIp = context.getString(R.string.default_server_ip);

        if (context.getApplicationContext() instanceof DelhiTransitApplication) {
            serverIp = ((DelhiTransitApplication) context.getApplicationContext()).getServerIP();
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .connectTimeout(2, TimeUnit.MINUTES);
        try {
            retrofit = new Retrofit.Builder()
                    .baseUrl(serverIp)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Please set valid server IP", Toast.LENGTH_SHORT).show();
        }
        return retrofit;
    }

    public static ApiInterface getApiService(Context context) {
        return getApiClient(context).create(ApiInterface.class);
    }
}
