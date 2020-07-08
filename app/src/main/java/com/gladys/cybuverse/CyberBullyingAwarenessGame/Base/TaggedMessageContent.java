package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import androidx.annotation.NonNull;

public class TaggedMessageContent extends MessageContent {
    private String tag;

    public TaggedMessageContent(String tag, Object content, String type) {
        super(content, type);
        this.tag = tag;
    }

    public TaggedMessageContent(String tag, Object content) {
        super(content);
        this.tag = tag;
    }

    public TaggedMessageContent(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @NonNull
    @Override
    public String toString() {
        return "TaggedMessageContent<" + getType() + ": (Tag:" + getTag() + ", Content:" + getData() + ")>";
    }
}
