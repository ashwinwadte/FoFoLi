package com.waftinc.fofoli.utils;

public class Constants {

    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where users are stored (ie "users")
     */
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_POSTS = "posts";
    public static final String FIREBASE_LOCATION_USER_INFO = "userInfo";
    public static final String FIREBASE_LOCATION_USER_POSTS = "userPosts";


    //Constant Firebase URLS
    public static final String FIREBASE_ROOT_URL = "https://fofoli.firebaseio.com";
    public static final String FIREBASE_URL_USERS = FIREBASE_ROOT_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_POSTS = FIREBASE_ROOT_URL + "/" + FIREBASE_LOCATION_POSTS;

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";

    //SharedPreference KEYs
    public static final String USER_NAME = "userName";
    public static final String USER_CONTACT = "userContact";
    public static final String USER_ADDRESS = "userAddress";

    public static final String USER_EMAIL = "userEmail";
    public static final String ENCODED_EMAIL = "encodedEmail";
    public static final String UID = "uid";
    public static final String FIREBASE_QUERY_TIMESTAMP = "timestampCreatedInverse";
    public static final String FIREBASE_QUERY_REQUEST_ACCEPTED = "requestAccepted";
}
