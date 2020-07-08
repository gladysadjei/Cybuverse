package com.gladys.cybuverse.Models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.HashMap;
import java.util.Map;

public class Question {

    private String chatUID,
            name,
            email,
            message,
            response,
            profileImageUri;

    @ServerTimestamp
    private Timestamp timestamp;

    private boolean isSeen, hasResponse;

    private Map<String, Object> properties;

    public Question(String chatUID, String name, String email, String profileImageUri, String message) {
        this.isSeen = false;
        this.hasResponse = false;
        this.chatUID = chatUID;
        this.name = name;
        this.email = email;
        this.message = message;
        this.profileImageUri = profileImageUri;
        this.properties = new HashMap<>();
    }

    public Question(String chatUID, User user, String message) {
        this.isSeen = false;
        this.hasResponse = false;
        this.message = message;
        this.chatUID = chatUID;
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileImageUri = user.getProfileUri();
        this.properties = new HashMap<>();
    }

    public Question() {
        this.isSeen = false;
        this.properties = new HashMap<>();
    }


    public String getChatUID() {
        return chatUID;
    }

    public void setChatUID(String chatUID) {
        this.chatUID = chatUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isHasResponse() {
        return hasResponse;
    }

    public void setHasResponse(boolean hasResponse) {
        this.hasResponse = hasResponse;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
