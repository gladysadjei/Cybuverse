package com.gladys.cybuverse.Models;

import java.util.HashMap;
import java.util.Map;

public class Avatar {

    private String gender, imageUri, thumbnailUri;
    private Map<String, Object> properties;

    public Avatar(String gender, String imageUri, String thumbnailUri) {
        this.gender = gender;
        this.imageUri = imageUri;
        this.thumbnailUri = thumbnailUri;
        this.properties = new HashMap<>();
    }

    public Avatar() {
        this.properties = new HashMap<>();
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(String thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
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
