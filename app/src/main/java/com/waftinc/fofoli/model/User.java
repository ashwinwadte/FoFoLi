package com.waftinc.fofoli.model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String contact;
    private String address;
    private Object timestampJoined;

    /**
     * Required public constructor
     */
    public User() {
    }

    public User(String name, String contact, String address) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.timestampJoined = com.google.firebase.database.ServerValue.TIMESTAMP;
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

    public Object getTimestampJoined() {
        return timestampJoined;
    }

    @Exclude
    public Map<String, Object> toMap(){
        // TODO: update in future
        HashMap<String, Object> result = new HashMap<>();
//        result.put("name", name);
        return result;
    }
}
