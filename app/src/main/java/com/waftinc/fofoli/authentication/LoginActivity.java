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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.waftinc.fofoli.MainActivity;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.model.User;
import com.waftinc.fofoli.utils.Constants;
import com.waftinc.fofoli.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends Activity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    /**
     * Data from the authenticated user
     */
    public static FirebaseAuth mAuth;

    @BindView(R.id.edit_text_email)
    EditText etEmail;
    @BindView(R.id.edit_text_password)
    EditText etPassword;
    @BindView(R.id.pbLogin)
    ProgressBar progressBar;
    @BindView(R.id.linear_layout_login_activity)
    LinearLayout linearLayout;

    private DatabaseReference mFirebaseRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseRootRef = FirebaseDatabase.getInstance().getReference();

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
        // If this is the case we can set the authenticated user and hide any login buttons
        mAuth.addAuthStateListener(mAuthStateListener);

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
        if (TextUtils.isEmpty(mPassword)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        } else if (!isPasswordValid(mPassword)) {
            etPassword.setError(getString(R.string.error_short_password));
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = task.getResult().getUser();

                            final String uid = user.getUid();
                            final String encodedEmail = Utils.encodeEmail(email);

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            final SharedPreferences.Editor spe = sp.edit();

                            DatabaseReference userInfoRef = mFirebaseRootRef.child(Constants.FIREBASE_LOCATION_USERS)
                                    .child(encodedEmail)
                                    .child(Constants.FIREBASE_LOCATION_USER_INFO);

                            userInfoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    try {
                                        User user = dataSnapshot.getValue(User.class);
                                        if (user != null) {
                                            spe.putString(Constants.USER_NAME, user.getName());
                                            spe.putString(Constants.USER_CONTACT, user.getContact());
                                            spe.putString(Constants.USER_ADDRESS, user.getAddress());

                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "error in parsing User: " + e);
                                    }
                                    spe.putString(Constants.USER_EMAIL, email);
                                    spe.putString(Constants.ENCODED_EMAIL, encodedEmail);
                                    spe.putString(Constants.UID, uid);
                                    spe.apply();

                                    //start new activity
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i(TAG, "user write error: " + databaseError.getMessage());
                                }
                            });


                        } else {
                            progressBar.setVisibility(View.GONE);
                            linearLayout.setVisibility(View.VISIBLE);

                            Exception e = task.getException();

                            if (e instanceof FirebaseAuthInvalidUserException) {
                                etEmail.requestFocus();
                                etEmail.setError(getString(R.string.error_user_not_registered));
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                etPassword.requestFocus();
                                etPassword.setError(getString(R.string.error_invalid_password));
                            } else {
                                etPassword.requestFocus();
                                showErrorDialog(getString(R.string.string_network_error));
                            }
                        }
                    }
                });
    }

    /**
     * Show errors to users
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.string_error))
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
