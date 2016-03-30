package com.waftinc.fofoli.posts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;
import com.waftinc.fofoli.R;
import com.waftinc.fofoli.utils.Constants;

/**
 * Adds a new shopping list
 */
public class NewPostDialogFragment extends DialogFragment {
    EditText etCount;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */
    public static NewPostDialogFragment newInstance() {
        return new NewPostDialogFragment();
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_new_post, null);
        etCount = (EditText) rootView.findViewById(R.id.edit_text_count_of_people);

        /**
         * Call postNewRequest() when user taps "Done" keyboard action
         */
        etCount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    postNewRequest();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout*/
        builder.setView(rootView)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_post, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //postNewRequest();
                    }
                });

        final AlertDialog alert = builder.create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do something
                        postNewRequest();
                    }
                });
            }
        });

        //return builder.create();
        return alert;
    }

    /**
     * Post new request
     */
    public void postNewRequest() {
        etCount.setError(null);

        String userEnteredCount = etCount.getText().toString();

        boolean cancel = false;
        View focusView = null;


        /**
         * If EditText input is not empty
         */
        if (TextUtils.isEmpty(userEnteredCount)) {
            etCount.setError(getString(R.string.error_field_required));
            focusView = etCount;
            cancel = true;
        } else if (Integer.parseInt(userEnteredCount) > 5) {
            Log.d("raju", userEnteredCount);
            etCount.setError(getString(R.string.error_max_people));
            focusView = etCount;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt posting and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            /* Get user data from sp*/
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String userName = sp.getString(Constants.USER_NAME, "User");
            String userContact = sp.getString(Constants.USER_CONTACT, "9876543210");
            String userAddress = sp.getString(Constants.USER_ADDRESS, "City, India");
            String userEmail = sp.getString(Constants.USER_EMAIL, "user@example.com");
            String encodedEmail = sp.getString(Constants.ENCODED_EMAIL, "user@example,com");

            /**
             * Create Firebase references
             */
            Firebase newPostRef = new Firebase(Constants.FIREBASE_URL_POSTS);
            Firebase userNewPostRef = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail).child(Constants.FIREBASE_LOCATION_USER_POSTS);


            /* Build the shopping list */
            Post newPost = new Post(userName, userContact, userAddress, userEmail, userEnteredCount, ServerValue.TIMESTAMP);

            //HashMap<String, Object> newPostMap = (HashMap<String, Object>) new ObjectMapper().convertValue(newPost, Map.class);

            Firebase newPostRefId = newPostRef.push();
            final String postId = newPostRefId.getKey();

            newPostRefId.setValue(newPost);
            userNewPostRef.child(postId).setValue(newPost);

            /* Close the dialog fragment */
            NewPostDialogFragment.this.getDialog().cancel();
        }
    }
}

