package com.waftinc.fofoli.model;

import com.firebase.client.ServerValue;
import com.waftinc.fofoli.utils.Constants;

import java.util.HashMap;

/**
 * Created by Ashwin on 27-Mar-16.
 */
public class User {
    private String name;
    private String contact;
    private String address;
    private HashMap<String, Object> timestampJoined;

    /**
     * Required public constructor
     */
    public User() {
    }

    public User(String name, String contact, String address, HashMap<String, Object> timestampJoined) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.timestampJoined = timestampJoined;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }
}
