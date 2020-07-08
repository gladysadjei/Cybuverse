package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Conversation {
    private Chat chat;
    private String description, aim;
    private List<Actor> actors;
    private List<Message> messages;
    private Stack<ConversationReader> interruptConversations;

    public Conversation(Chat chat, String description, String aim, List<Actor> actors, List<Message> messages, Stack<ConversationReader> interruptConversations) {
        this.chat = chat;
        this.description = description;
        this.aim = aim;
        this.actors = actors;
        this.messages = messages;
        this.interruptConversations = interruptConversations;
    }

    public Conversation(Chat chat, String description, String aim) {
        this.chat = chat;
        this.description = description;
        this.aim = aim;
        actors = new ArrayList<>();
        messages = new ArrayList<>();
        this.interruptConversations = new Stack<>();
    }

    public Conversation(Chat chat) {
        this.chat = chat;
        actors = new ArrayList<>();
        messages = new ArrayList<>();
        this.interruptConversations = new Stack<>();
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public void addActor(Actor... actors) {
        this.actors.addAll(Arrays.asList(actors));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if (actors.contains(message.getSender()) &&
                (actors.containsAll(message.getReceivers()) || message.getReceivers() == null)) {
            this.messages.add(message);
        }
    }

    public void addMessage(Actor sender, List<Actor> receivers, MessageContent content) {
        if (actors.contains(sender) && receivers != null && actors.containsAll(receivers)) {
            this.messages.add(new Message(sender, receivers, content));
        }
    }

    public void addMessage(Actor sender, Actor receiver, MessageContent content) {
        if (actors.contains(sender) && (actors.contains(receiver) || receiver == null)) {
            this.messages.add(new Message(sender, receiver, content));
        }
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

    public Stack<ConversationReader> getInterruptConversations() {
        return interruptConversations;
    }

    public void setInterruptConversations(Stack<ConversationReader> interruptConversations) {
        this.interruptConversations = interruptConversations;
    }

    public void addInterruptMessage(Message ...messages) {
        Conversation conversation = new Conversation(null,null,null,getActors(),Arrays.asList(messages), new Stack<ConversationReader>());
        addInterruptConversation(conversation);
    }

    public void addInterruptConversation(Conversation ...conversations) {
        addInterruptConversation(Arrays.asList(conversations));
    }

    public void addInterruptConversation(List<Conversation> conversations) {
        for (int i=conversations.size()-1;i > -1;i--){
            this.interruptConversations.push(new ConversationReader(conversations.get(i)));
        }
    }

    private void addInterruptConversation(Stack<Conversation> conversationStack) {
        List<Conversation> messages =  new ArrayList<>();
        while (!conversationStack.isEmpty()) {
            messages.add(conversationStack.pop());
        }
        addInterruptConversation(messages);
    }

    public boolean hasInterruptConversation() {
        return !getInterruptConversations().isEmpty();
    }

    public ConversationReader popInterruptConversation() {
        return getInterruptConversations().pop();
    }

    public ConversationReader peekInterruptConversation() {
        return getInterruptConversations().peekTop();
    }

    @Override
    public String toString() {
        return "Conversation<" + this.getMessages().size() + ">";
    }

}
