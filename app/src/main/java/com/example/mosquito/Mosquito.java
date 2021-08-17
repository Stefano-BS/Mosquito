package com.example.mosquito;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

public class Mosquito extends Application {
    public static Context context;
    private static Mosquito instance;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        instance = this;
    }

    public static boolean internet() {
        ConnectivityManager cm = (ConnectivityManager) instance.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    public static float convertDpToPixel(int dp){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}