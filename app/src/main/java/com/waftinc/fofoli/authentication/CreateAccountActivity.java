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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.waftinc.fofoli.MainActivity;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.Utils;

import java.util.Map;

public class CreateAccountActivity extends Activity {

    /**
     * Data from the authenticated user
     */
    public static AuthData mAuthData;

    EditText etName, etContact, etAddress, etEmail, etNewPassword, etConfirmPassword;
    ProgressBar progressBar;

    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mFirebaseRef = new Firebase(Constants.FIREBASE_ROOT_URL);

        initWidgets();

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

    private void initWidgets() {
        etName = (EditText) findViewById(R.id.edit_text_username_create);
        etContact = (EditText) findViewById(R.id.edit_text_mobile_create);
        etAddress = (EditText) findViewById(R.id.edit_text_address_create);
        etEmail = (EditText) findViewById(R.id.edit_text_email_create);
        etNewPassword = (EditText) findViewById(R.id.edit_text_new_password);
        etConfirmPassword = (EditText) findViewById(R.id.edit_text_confirm_password);
        progressBar = (ProgressBar) findViewById(R.id.pbCreate);
    }

    public void onSignInPressed(View view) {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onCreateAccountPressed(View view) {
        createNewUser();
    }

    private void createNewUser() {
        // Reset errors.
        etName.setError(null);
        etContact.setError(null);
        etAddress.setError(null);
        etEmail.setError(null);
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);

        // get the values
        String mName = etName.getText().toString();
        String mContact = etContact.getText().toString();
        String mAddress = etAddress.getText().toString();
        final String mEmail = etEmail.getText().toString();
        final String mNewPassword = etNewPassword.getText().toString();
        String mConfirmPassword = etConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!mNewPassword.equals(mConfirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_password_mismatch));
            focusView = etConfirmPassword;
            cancel = true;
        }

        if (!TextUtils.isEmpty(mNewPassword) && !isPasswordValid(mNewPassword)) {
            etNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = etNewPassword;
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

        if (TextUtils.isEmpty(mAddress)) {
            etAddress.setError(getString(R.string.error_field_required));
            focusView = etAddress;
            cancel = true;
        }

        if (TextUtils.isEmpty(mContact)) {
            etContact.setError(getString(R.string.error_field_required));
            focusView = etContact;
            cancel = true;
        }

        if (TextUtils.isEmpty(mName)) {
            etName.setError(getString(R.string.error_field_required));
            focusView = etName;
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

            mFirebaseRef.createUser(mEmail, mNewPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Toast.makeText(getApplicationContext(), "New account created", Toast.LENGTH_SHORT).show();
                    loginWithPassword(mEmail, mNewPassword);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    // there was an error
                    progressBar.setVisibility(View.GONE);
                    etConfirmPassword.requestFocus();
                    showErrorDialog("Error: " + firebaseError.getMessage());
                }
            });
        }
    }

    public void loginWithPassword(final String email, final String password) {

        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {
                progressBar.setVisibility(View.GONE);

                String userEmail = authData.getProviderData().get("email").toString();
                String uid = authData.getUid();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateAccountActivity.this);
                SharedPreferences.Editor spe = sp.edit();

                spe.putString(Constants.USER_EMAIL, userEmail);
                spe.putString(Constants.ENCODED_EMAIL, Utils.encodeEmail(userEmail));
                spe.putString(Constants.UID, uid);
                spe.apply();

                //start new activity
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                showErrorDialog("Network problem!\nPlease try again!");

                //go to login activity
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }

    private boolean isEmailValid(String email) {

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
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
}
