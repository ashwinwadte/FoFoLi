package com.waftinc.fofoli.model;

/**
 * Created by Ashwin on 30-Mar-16.
 */
public class Post {
    String providerName;
    String providerContact;
    String providerAddress;
    String providerEmail;
    String peopleCount;
    boolean requestAccepted;
    String distributor;
    Object timestampCreated;
    Object timestampRequestAccepted;

    public Post() {
    }

    public Post(String providerName, String providerContact, String providerAddress, String providerEmail, String peopleCount, Object timestampCreated) {
        this.providerName = providerName;
        this.providerContact = providerContact;
        this.providerAddress = providerAddress;
        this.providerEmail = providerEmail;
        this.peopleCount = peopleCount;
        this.timestampCreated = timestampCreated;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getProviderContact() {
        return providerContact;
    }

    public String getProviderAddress() {
        return providerAddress;
    }

    public String getProviderEmail() {
        return providerEmail;
    }

    public String getPeopleCount() {
        return peopleCount;
    }

    public boolean isRequestAccepted() {
        return requestAccepted;
    }

    public String getDistributor() {
        return distributor;
    }

    public Object getTimestampCreated() {
        return timestampCreated;
    }

    public Object getTimestampRequestAccepted() {
        return timestampRequestAccepted;
    }

    public void setRequestAccepted(boolean requestAccepted) {
        this.requestAccepted = requestAccepted;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public void setTimestampRequestAccepted(Object timestampRequestAccepted) {
        this.timestampRequestAccepted = timestampRequestAccepted;
    }
}
