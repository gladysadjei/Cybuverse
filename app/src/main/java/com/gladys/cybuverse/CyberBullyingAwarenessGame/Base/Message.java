package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import com.gladys.cybuverse.Helpers.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {

    private Actor sender;
    private List<Actor> receivers;
    private MessageContent content;
    private Map<String, Object> properties;
    private OnReadListener onReadListener;

    public Message(Actor sender, List<Actor> receivers, MessageContent content) {
        this.content = content;
        this.sender = sender;
        this.receivers = receivers;
        this.properties = new HashMap<>();
    }

    public Message(Actor sender, Actor receiver, MessageContent content) {
        this.content = content;
        this.sender = sender;
        this.receivers = new ArrayList<>();
        this.receivers.add(receiver);
        this.properties = new HashMap<>();
    }

    public Message(Actor sender, List<Actor> receivers) {
        this.content = new MessageContent();
        this.sender = sender;
        this.receivers = receivers;
        this.properties = new HashMap<>();
    }

    public Message(Actor sender, Actor receiver) {
        this.content = new MessageContent();
        this.sender = sender;
        this.receivers = new ArrayList<>();
        this.receivers.add(receiver);
        this.properties = new HashMap<>();
    }

    public Message() {
        this.content = new MessageContent();
        this.receivers = new ArrayList<>();
        this.properties = new HashMap<>();
    }

    public MessageContent getContent() {
        return content;
    }

    public Message setContent(MessageContent content) {
        this.content = content;
        return this;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Message setProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Message setProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public boolean hasProperty(String key) {
        return this.properties.containsKey(key);
    }

    public Actor getSender() {
        return sender;
    }

    public Message setSender(Actor sender) {
        this.sender = sender;
        return this;
    }

    public List<Actor> getReceivers() {
        return receivers;
    }

    public Message setReceivers(List<Actor> receivers) {
        this.receivers = receivers;
        return this;
    }

    public OnReadListener getOnReadListener() {
        return onReadListener;
    }

    public Message setOnReadListener(OnReadListener onReadListener) {
        this.onReadListener = onReadListener;
        return this;
    }

    public boolean isEmptyContent() {
        return getContent().isEmpty();
    }

    public void read() {
        if (onReadListener != null) {
            onReadListener.read(this);
        }
    }

    @Override
    public String toString() {
        return "Message<" + getSender() + " => " + getReceivers() + " :: " + getContent() + ">";
    }

    public interface OnReadListener {
        void read(Message message);
    }

}
