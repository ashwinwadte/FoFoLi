package com.waftinc.fofoli;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class FoFoLiApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
