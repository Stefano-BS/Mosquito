package com.example.mosquito;

import android.app.Application;
import android.content.Context;

public class Mosquito extends Application {
    public static Context context;

    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}