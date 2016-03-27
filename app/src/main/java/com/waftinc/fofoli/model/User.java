package com.waftinc.fofoli.model;

/**
 * Created by Ashwin on 27-Mar-16.
 */
public class User {
    private String name;
    private String contact;
    private String address;

    /**
     * Required public constructor
     */
    public User() {
    }

    public User(String name, String contact, String address) {
        this.name = name;
        this.contact = contact;
        this.address = address;
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
}
