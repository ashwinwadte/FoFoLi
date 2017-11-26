package com.waftinc.fofoli;

import android.app.Application;

import com.firebase.client.Firebase;

public class FoFoLiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
