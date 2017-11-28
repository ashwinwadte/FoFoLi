package com.waftinc.fofoli.utils;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;
import com.waftinc.fofoli.model.Post;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    //Date format for Image file name
    public static final String SIMPLE_DATE_FORMAT = "yyyyMMddHHmmss";

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
}
