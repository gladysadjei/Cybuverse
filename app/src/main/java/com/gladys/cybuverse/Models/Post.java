package com.gladys.cybuverse.Models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class Post {

    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_FILE = "file";
    public static final String TYPE_NO_MEDIA = "no-media";

    private String type, message, mediaUri, link;
    @ServerTimestamp
    private Date timestamp;
    private Map<String, Object> properties;

    public Post(String type, String message, String mediaUri, String link, Date timestamp, Map<String, Object> properties) {
        this.type = type;
        this.message = message;
        this.mediaUri = mediaUri;
        this.link = link;
        this.timestamp = timestamp;
        this.properties = properties;
        if (properties == null)
            this.properties = new HashMap<>();
    }

    public Post(String type, String message, String mediaUri, String link, Date timestamp) {
        this.type = type;
        this.message = message;
        this.mediaUri = mediaUri;
        this.link = link;
        this.timestamp = timestamp;
        this.properties = new HashMap<>();
    }

    public Post() {
        this.properties = new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
        if (properties == null)
            this.properties = new HashMap<>();
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
        return "Post<" + ((getType() == null) ? "Empty" : getType()) + ">";
    }
}
