package com.waftinc.fofoli;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Ashwin on 27-Mar-16.
 */
public class FoFoLiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
