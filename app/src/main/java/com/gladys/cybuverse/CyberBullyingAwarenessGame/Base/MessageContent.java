package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import androidx.annotation.NonNull;

public class MessageContent {
    public static final String TYPE_MEDIA = "media";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_ALL = "all";
    public static final String TYPE_ANY = "any";

    private Object content;
    private String type;

    public MessageContent(Object content, String type) {
        this.content = content;
        this.type = type;
    }

    public MessageContent(Object content) {
        this.content = content;
        this.type = TYPE_ALL;
    }

    public MessageContent() {
        this.type = TYPE_ANY;
    }

    public Object getData() {
        return content;
    }

    public void setData(Object content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean matches(MessageContent messageContent) {
        return ((getType().equals(messageContent.getType()) ||
                getType().equals(TYPE_ALL) || getType().equals(TYPE_ANY)) &&
                getData().toString().trim().toLowerCase()
                        .equals(messageContent.getData().toString().trim().toLowerCase()));
    }

    public boolean matchesStrictly(MessageContent messageContent) {
        return (getType().equals(messageContent.getType()) &&
                getData().toString().equals(messageContent.getData().toString()));
    }

    public boolean isEmpty() {
        return content == null;
    }

    @NonNull
    @Override
    public String toString() {
        return "MessageContent<" + getType() + ": " + getData() + ">";
    }
}
