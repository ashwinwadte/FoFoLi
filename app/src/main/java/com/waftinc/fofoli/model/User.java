package com.waftinc.fofoli.model;

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

    public User(String name, String contact, String address, Object timestampJoined) {
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

    public Object getTimestampJoined() {
        return timestampJoined;
    }
}
