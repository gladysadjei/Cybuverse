package com.gladys.cybuverse.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class ChatMessage {
    private String sender, content, mediaType;
    private List<String> mediaUriList;
    @ServerTimestamp
    private Date timestamp;
    private Map<String, Object> properties;

    public ChatMessage(String sender, String content, String mediaType, List<String> mediaUriList) {
        this.sender = sender;
        this.content = content;
        this.mediaType = mediaType;
        this.mediaUriList = mediaUriList;
        this.properties = new HashMap<>();
    }

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
        this.properties = new HashMap<>();
        this.mediaUriList = new ArrayList<>();
    }

    public ChatMessage() {
        this.properties = new HashMap<>();
        this.mediaUriList = new ArrayList<>();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public List<String> getMediaUriList() {
        return mediaUriList;
    }

    public void setMediaUriList(List<String> mediaUriList) {
        this.mediaUriList = mediaUriList;
    }

    public void addMediaUriList(String mediaUri) {
        this.mediaUriList.add(mediaUri);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        if (properties == null)
            this.properties = new HashMap<>();
        this.properties = properties;
    }


    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void addProperty(String key, Object value) {


        this.properties.put(key, value);

    }

    public boolean hasProperty(String key) {
        if (this.properties != null) {
            return this.properties.containsKey(key);
        }
        return false;
    }


    @NonNull
    @Override
    public String toString() {
        return "ChatMessage<" + getSender() + ":" + getContent() + ">";
    }
}
