package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class Scene {
    private List<Conversation> conversations;
    private String name;
    private String description;
    private String aim;

    public Scene(List<Conversation> conversations, String name, String description, String aim) {
        this.conversations = new ArrayList<>();
        if (conversations != null)
            this.conversations.addAll(conversations);
        this.name = name;
        this.description = description;
        this.aim = aim;
    }

    public Scene(String name, String description, String aim) {
        this.conversations = new ArrayList<>();
        this.name = name;
        this.description = description;
        this.aim = aim;
    }

    public Scene(List<Conversation> conversations, String name) {
        this.conversations = new ArrayList<>();
        if (conversations != null)
            this.conversations.addAll(conversations);
        this.name = name;
    }

    public Scene(String name) {
        this.conversations = new ArrayList<>();
        this.name = name;
    }

    public List<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(List<Conversation> conversations) {
        this.conversations = conversations;
    }

    public void addConversation(Conversation conversation) {
        this.conversations.add(conversation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    @NonNull
    @Override
    public String toString() {
        return "Scene<" + getName() + ", " + getConversations() + ">";
    }
}
