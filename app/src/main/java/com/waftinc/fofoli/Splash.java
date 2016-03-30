package com.waftinc.fofoli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.waftinc.fofoli.authentication.LoginActivity;
import com.waftinc.fofoli.utils.Constants;

public class Splash extends Activity {
    /**
     * Data from the authenticated user
     */
    public static AuthData mAuthData;

    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mFirebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mAuthData != null) {
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Splash.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuthData = mFirebaseRef.getAuth();
    }
}
