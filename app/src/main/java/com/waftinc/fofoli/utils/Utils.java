package com.waftinc.fofoli.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.waftinc.fofoli.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    //Date format for Image file name
    private static final String SIMPLE_DATE_FORMAT = "yyyyMMddHHmmss";

    // adding a private constructor to the Utils class to prevent the accidental instantiation.
    // It will make the maintenance easier.
    private Utils() {
    }

    //TODO: remove unnecessary thing
    public static String getSubjectID(String encodedEmail) {
        String timeStamp = new SimpleDateFormat(Utils.SIMPLE_DATE_FORMAT, Locale.US).format(new Date());

        return encodedEmail + timeStamp;
    }

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    /**
     * Email is being decoded just once to display real email
     */
    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

    public static FirebaseRecyclerOptions<Post> getFirebaseRecyclerOptions(Query query) {
        return new FirebaseRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
    }

    public static void navigateToMap(Context context, String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mapIntent);
    }
}
