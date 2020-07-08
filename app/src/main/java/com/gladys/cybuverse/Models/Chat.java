package com.gladys.cybuverse.Models;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class Chat {
    private String id, name, lastMessage, lastSeen;
    private int unreadMessagesCount;
    private Map<String, Object> properties;

    public Chat(String name, String lastMessage, String lastSeen, int unreadMessagesCount) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.lastSeen = lastSeen;
        this.unreadMessagesCount = unreadMessagesCount;
        this.properties = new HashMap<>();
    }

    public Chat(String name) {
        this.name = name;
        this.properties = new HashMap<>();
    }

    public Chat() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public int getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(int unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void addProperty(String key, Object value) {
        getProperties().put(key, value);
    }

    public Object getProperty(String key) {
        return getProperties().get(key);
    }

    public boolean hasProperty(String key) {
        return getProperty(key) != null;
    }

    @NonNull
    @Override
    public String toString() {
        return "Chat<" + ((getName() == null) ? "Empty" : getName()) + ">";
    }
}
