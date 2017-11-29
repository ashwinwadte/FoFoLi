package com.waftinc.fofoli;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.waftinc.fofoli.authentication.LoginActivity;

public class SplashActivity extends Activity {
    public static final long DELAY_MILLIS = 3000;

    // Data from the authenticated user
    private static FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mCurrentUser != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, DELAY_MILLIS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user is signed in
        mCurrentUser = mAuth.getCurrentUser();
    }
}
