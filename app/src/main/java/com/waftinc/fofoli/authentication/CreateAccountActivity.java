package com.waftinc.fofoli.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.waftinc.fofoli.MainActivity;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.model.User;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.TextDrawable;
import com.waftinc.fofoli.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends Activity {
    private static final String TAG = CreateAccountActivity.class.getSimpleName();

    /**
     * Data from the authenticated user
     */
    private static FirebaseAuth mAuth;

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @BindView(R.id.edit_text_username_create)
    EditText etName;
    @BindView(R.id.edit_text_mobile_create)
    EditText etContact;
    @BindView(R.id.edit_text_email_create)
    EditText etEmail;
    @BindView(R.id.edit_text_new_password)
    EditText etNewPassword;
    @BindView(R.id.edit_text_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.tvGetAddress)
    TextView tvGetAddress;
    @BindView(R.id.pbCreate)
    ProgressBar progressBar;
    @BindView(R.id.linear_layout_create_account_activity)
    LinearLayout linearLayout;

    private DatabaseReference mFirebaseRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mFirebaseRootRef = FirebaseDatabase.getInstance().getReference();

        String mCountryCode = Constants.INDIA_CODE;

        int dpPad = (int) (12 * getResources().getDisplayMetrics().scaledDensity);

        etContact.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(mCountryCode, CreateAccountActivity.this),
                null, null, null);
        etContact.setCompoundDrawablePadding(mCountryCode.length() * dpPad);

        // Listener for Firebase session changes
        FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mAuth = firebaseAuth;

                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.i(TAG, "AuthStateChanged: User is signed in with uid: " + user.getUid());
                } else {
                    Log.i(TAG, "AuthStateChanged: No user is signed in.");
                }
            }
        };

        // Check if the user is authenticated with Firebase already.
        // If this is the case we can set the authenticated user and hide any login buttons.
        mAuth.addAuthStateListener(mAuthStateListener);

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
        tvGetAddress.setError(null);
        etEmail.setError(null);
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);

        // get the values
        final String mName = etName.getText().toString();
        final String mContact = etContact.getText().toString();
        final String mAddress = tvGetAddress.getText().toString();
        final String mEmail = etEmail.getText().toString();
        final String mNewPassword = etNewPassword.getText().toString();
        final String mConfirmPassword = etConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mNewPassword)) {
            etNewPassword.setError(getString(R.string.error_field_required));
            focusView = etNewPassword;
            cancel = true;
        } else if (!mNewPassword.equals(mConfirmPassword)) {
            etConfirmPassword.setError(getString(R.string.error_password_mismatch));
            focusView = etConfirmPassword;
            cancel = true;
        } else if (!isPasswordValid(mNewPassword)) {
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
            tvGetAddress.setError(getString(R.string.error_field_required));
            focusView = tvGetAddress;
            cancel = true;
        }

        if (TextUtils.isEmpty(mContact)) {
            etContact.setError(getString(R.string.error_field_required));
            focusView = etContact;
            cancel = true;
        } else if (mContact.length() != 10) {
            etContact.setError(getString(R.string.error_ten_digits_number));
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


            final User newUser = new User(mName, mContact, mAddress);

            mAuth.createUserWithEmailAndPassword(mEmail, mNewPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up successful
                                Toast.makeText(getApplicationContext(), R.string.string_new_account_created, Toast
                                        .LENGTH_SHORT)
                                        .show();

                                addUserToFirebase(newUser, mEmail);
                                loginWithPassword(mEmail, mNewPassword, newUser);
                            } else {
                                // Sign up failed
                                progressBar.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);

                                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                    etEmail.requestFocus();
                                    etEmail.setError(getString(R.string.error_email_taken));
                                } else {
                                    etConfirmPassword.requestFocus();
                                    showErrorDialog(getString(R.string.string_network_error));
                                }
                            }
                        }
                    });
        }
    }

    private void loginWithPassword(final String email, final String password, final User newUser) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = task.getResult().getUser();

                            String uid = user.getUid();

                            SharedPreferences sp = PreferenceManager
                                    .getDefaultSharedPreferences(CreateAccountActivity.this);
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
                        } else {
                            progressBar.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);

                            showErrorDialog(getString(R.string.string_network_error));

                            //go to login activity
                            Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                    }
                });
    }

    private void addUserToFirebase(User user, String mEmail) {
        String encodedEmail = Utils.encodeEmail(mEmail);

        DatabaseReference userInfo = mFirebaseRootRef.child(Constants.FIREBASE_LOCATION_USERS).child(encodedEmail)
                .child(Constants.FIREBASE_LOCATION_USER_INFO);

        userInfo.setValue(user);
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
                .setTitle(R.string.string_error)
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

                //etAddress.setText(place.getName());

                String address = (String) place.getAddress();
                Log.i(TAG, "Place name: " + place.getName() + " address: " + place.getAddress() + " lat-lang: " + place
                        .getLatLng());

                tvGetAddress.setText(address);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Log.i(TAG, "user cancelled the operation");
            }
        }
    }

    public void onGetAddressPressed(View view) {
        openMapPlaceFragment();
    }

    private void openMapPlaceFragment() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                    .build();


            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setBoundsBias(new LatLngBounds(
                                    new LatLng(Constants.SOUTHWEST_LAT, Constants.SOUTHWEST_LONG),
                                    new LatLng(Constants.NORTHEAST_LAT, Constants.NORTHEAST_LONG)))
                            .setFilter(typeFilter)
                            .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Google Play Service Error: " + e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Google Play Service Not available: " + e);
        }
    }


}
