package com.waftinc.fofoli.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Ashwin on 27-Mar-16.
 */
public class Utils {

    //Date format for Image file name
    public static final String SIMPLE_DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String SIMPLE_DATE_FORMAT1 = "yyyyMMddHHmmss";

    //TODO: remove unnecessary thing
    public static String getSubjectID(String encodedEmail) {
        String timeStamp = new SimpleDateFormat(Utils.SIMPLE_DATE_FORMAT1, Locale.US).format(new Date());

        String imageName = encodedEmail + timeStamp;

        return imageName;
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



}
