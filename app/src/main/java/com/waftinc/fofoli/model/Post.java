package com.waftinc.fofoli.model;

import com.waftinc.fofoli.R;

import java.util.Date;

public class Post {
    private String providerName;
    private String providerContact;
    private String providerAddress;
    private String providerEmail;
    private String peopleCount;
    private boolean requestAccepted;
    private String distributor;
    private Object timestampCreated;
    private Object timestampCreatedInverse;
    private Object timestampRequestAccepted;

    public Post() {
    }

    public Post(String providerName, String providerContact, String providerAddress, String providerEmail, String
            peopleCount, Object timestampCreated) {
        this.providerName = providerName;
        this.providerContact = R.string.india_code + providerContact;
        this.providerAddress = providerAddress;
        this.providerEmail = providerEmail;
        this.peopleCount = peopleCount;
        this.timestampCreated = timestampCreated;
        this.timestampCreatedInverse = -1 * (new Date().getTime());

        this.requestAccepted = false;
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

    public void setRequestAccepted(boolean requestAccepted) {
        this.requestAccepted = requestAccepted;
    }

    public String getDistributor() {
        return distributor;
    }

    public void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public Object getTimestampCreated() {
        return timestampCreated;
    }

    public Object getTimestampRequestAccepted() {
        return timestampRequestAccepted;
    }

    public void setTimestampRequestAccepted(Object timestampRequestAccepted) {
        this.timestampRequestAccepted = timestampRequestAccepted;
    }

    public Object getTimestampCreatedInverse() {
        return timestampCreatedInverse;
    }
}
