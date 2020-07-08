package com.gladys.cybuverse.Models;

import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Message;

import androidx.annotation.NonNull;

public class ParcelableMessage {

    private int id;
    private String sender, content;

    public ParcelableMessage(int id, String sender, String content) {
        this.id = id;
        this.sender = sender;
        this.content = content;
    }

    public ParcelableMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public ParcelableMessage(Message message) {
        this.sender = message.getSender().getName();
        this.content = message.getContent().getData().toString();
    }

    public ParcelableMessage() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @NonNull
    @Override
    public String toString() {
        return "ParcelableMessage<" + getSender() + ":" + getContent() + ">";
    }

}
