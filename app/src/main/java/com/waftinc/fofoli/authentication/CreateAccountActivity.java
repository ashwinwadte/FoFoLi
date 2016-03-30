package com.waftinc.fofoli.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.waftinc.fofoli.MainActivity;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.model.User;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.Utils;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends Activity {

    /**
     * Data from the authenticated user
     */
    public static AuthData mAuthData;
    final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText etName, etContact, etAddress, etEmail, etNewPassword, etConfirmPassword;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

//        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.createAccountLayout);
//        relativeLayout.setBackgroundResource(R.drawable.background_loginscreen);

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
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
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
        final String mName = etName.getText().toString();
        final String mContact = etContact.getText().toString();
        final String mAddress = etAddress.getText().toString();
        final String mEmail = etEmail.getText().toString();
        final String mNewPassword = etNewPassword.getText().toString();
        final String mConfirmPassword = etConfirmPassword.getText().toString();

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
            linearLayout.setVisibility(View.GONE);


            final User newUser = new User(mName, mContact, mAddress, ServerValue.TIMESTAMP);

            mFirebaseRef.createUser(mEmail, mNewPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    Toast.makeText(getApplicationContext(), "New account created", Toast.LENGTH_SHORT).show();
                    addUserToFirebase(newUser, mEmail);
                    loginWithPassword(mEmail, mNewPassword, newUser);
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);

                    if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                        etEmail.requestFocus();
                        etEmail.setError(getString(R.string.error_email_taken));
                    } else {
                        etConfirmPassword.requestFocus();
                        showErrorDialog("Network problem!\nPlease try again!");
                    }
                }
            });
        }
    }

    private void loginWithPassword(final String email, final String password, final User newUser) {

        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {

            @Override
            public void onAuthenticated(AuthData authData) {


//                String userEmail = authData.getProviderData().get("email").toString();
                String uid = authData.getUid();

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CreateAccountActivity.this);
                SharedPreferences.Editor spe = sp.edit();

                spe.putString(Constants.USER_NAME, newUser.getName());
                spe.putString(Constants.USER_CONTACT, newUser.getContact());
                spe.putString(Constants.USER_ADDRESS, newUser.getAddress());


                spe.putString(Constants.USER_EMAIL, email);
                spe.putString(Constants.ENCODED_EMAIL, Utils.encodeEmail(email));
                spe.putString(Constants.UID, uid);
                spe.apply();

                //start new activity
                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

                progressBar.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);

                showErrorDialog("Network problem!\nPlease try again!");

                //go to login activity
                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
    }

    private void addUserToFirebase(User user, String mEmail) {
        String encodedEmail = Utils.encodeEmail(mEmail);

        Firebase userInfo = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail).child(Constants.FIREBASE_LOCATION_USER_INFO);

        HashMap<String, Object> newUserMap = (HashMap<String, Object>) new ObjectMapper().convertValue(user, Map.class);

        userInfo.updateChildren(newUserMap);
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


    public void onEditAddressPressed(View view) {
        openMapPlaceFragment();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("rajuP", "Place: " + place.getName());
                etAddress.setText(place.getName());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("rajuP1", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void onGetAddressPressed(View view) {
        openMapPlaceFragment();
    }

    private void openMapPlaceFragment() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }
}
