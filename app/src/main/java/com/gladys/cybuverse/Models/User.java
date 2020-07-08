package com.gladys.cybuverse.Models;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String name;
    private String gender;
    private String email;
    private String profile_uri;
    private Map<String, Object> info;

    public User() {
        this.profile_uri = "default";
        this.info = new HashMap<>();
        init();
    }

    public User(String name, String gender, String email) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.profile_uri = "default";
        this.info = new HashMap<>();
        init();
    }

    public User(String name, String gender, String email, Map<String, Object> info) {
        this.name = name;
        this.gender = gender;
        this.email = email;
        this.profile_uri = "default";
        this.info = info;
        init();
    }

    private void init() {
        addInfo("points", 0);
        addInfo("is-admin", false);
        addInfo("is-new-user", true);
        addInfo("scene-index", 0);
        addInfo("conversation-index", 0);
        addInfo("last-message-index", 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileUri() {
        return profile_uri;
    }

    public void setProfileUri(String profile_uri) {
        this.profile_uri = profile_uri;
    }

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public Object getInfo(String key) {
        return info.get(key);
    }

    public void addInfo(String key, Object value) {
        if (this.info != null) {
            this.info.put(key, value);
        }
    }

    public boolean hasInfoKey(String key) {
        if (this.info != null) {
            return this.info.containsKey(key);
        }
        return false;
    }

    public boolean hasInfoValue(String value) {
        if (this.info != null) {
            return this.info.containsValue(value);
        }
        return false;
    }
}
