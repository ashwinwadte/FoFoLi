package com.waftinc.fofoli.utils;

/**
 * Created by Ashwin on 27-Mar-16.
 */
public class Constants {

    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where users are stored (ie "users")
     */
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_POSTS = "posts";

    //Constant Firebase URLS
    public static final String FIREBASE_ROOT_URL = "your_firebase_app_url";
    public static final String FIREBASE_URL_USERS = FIREBASE_ROOT_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_POSTS = FIREBASE_ROOT_URL + "/" + FIREBASE_LOCATION_POSTS;

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";

    //SharedPreference KEYs
    public static final String USER_EMAIL = "userEmail";
    public static final String ENCODED_EMAIL = "encodedEmail";
    public static final String UID = "uid";
}
