package com.waftinc.fofoli.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.waftinc.fofoli.MainActivity;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.Utils;

public class LoginActivity extends Activity {
    /**
     * Data from the authenticated user
     */
    public static AuthData mAuthData;

    EditText etEmail, etPassword;
    ProgressBar progressBar;
    LinearLayout linearLayout;

    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.loginLayout);
//        relativeLayout.setBackgroundResource(R.drawable.background_loginscreen);

        mFirebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_login_activity);
        etEmail = (EditText) findViewById(R.id.edit_text_email);
        etPassword = (EditText) findViewById(R.id.edit_text_password);
        progressBar = (ProgressBar) findViewById(R.id.pbLogin);

        /**
         * Listener for Firebase session changes
         */
        Firebase.AuthStateListener mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                mAuthData = authData;
            }
        };

        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide any login buttons */
        mFirebaseRef.addAuthStateListener(mAuthStateListener);
    }

    /**
     * Open CreateAccountActivity when user taps on "Sign up" TextView
     */
    public void onSignUpPressed(View view) {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void onSignInPressed(View view) {
        attemptLogin();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        // Store values at the time of the login attempt.
        String mEmail = etEmail.getText().toString();
        String mPassword = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(mPassword) && !isPasswordValid(mPassword)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mEmail)) {
            etEmail.setError(getString(R.string.error_field_required));
            focusView = etEmail;
            cancel = true;
        } else if (!isEmailValid(mEmail)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressBar.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            loginWithPassword(mEmail, mPassword);

        }
    }

    private boolean isEmailValid(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    public void loginWithPassword(final String email, final String password) {

        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

                String userEmail = authData.getProviderData().get("email").toString();
                String uid = authData.getUid();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                SharedPreferences.Editor spe = sp.edit();

                spe.putString(Constants.USER_EMAIL, email);
                spe.putString(Constants.ENCODED_EMAIL, Utils.encodeEmail(email));
                spe.putString(Constants.UID, uid);
                spe.apply();

                //start new activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                if (firebaseError.getCode() == FirebaseError.USER_DOES_NOT_EXIST) {
                    etEmail.requestFocus();
                    etEmail.setError(getString(R.string.error_user_not_registered));
                } else {
                    etPassword.requestFocus();
                    showErrorDialog("Network problem!\nPlease try again!");
                }

            }
        });
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_error_outline_24px)
                .show();
    }

    /**
     * Show message to users
     */
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(R.drawable.ic_error_outline_24px)
                .show();
    }
}
