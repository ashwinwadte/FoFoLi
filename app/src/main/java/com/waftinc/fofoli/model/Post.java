package com.waftinc.fofoli.model;

import com.google.firebase.database.Exclude;
import com.waftinc.fofoli.utils.Constants;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
            peopleCount) {
        this.providerName = providerName;
        this.providerContact = Constants.INDIA_CODE + providerContact;
        this.providerAddress = providerAddress;
        this.providerEmail = providerEmail;
        this.peopleCount = peopleCount;
        this.timestampCreated = com.google.firebase.database.ServerValue.TIMESTAMP;
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

    public void setRequestAccepted() {
        this.requestAccepted = true;
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

    public void setTimestampRequestAccepted() {
        this.timestampRequestAccepted = com.google.firebase.database.ServerValue.TIMESTAMP;
    }

    public Object getTimestampCreatedInverse() {
        return timestampCreatedInverse;
    }

    @Exclude
    public Map<String, Object> toMap() {
        // TODO: update in future
        HashMap<String, Object> result = new HashMap<>();
//        result.put("providerName", providerName);
        return result;
    }
}
