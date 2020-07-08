package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Dictionary;

import java.util.List;

public class SceneBuilder {

    private Conversation conversation;
    private Dictionary<String, Object> properties;

    public SceneBuilder() {
        conversation = new Conversation(new Chat("Random"));
        properties = new Dictionary<>();
    }

    public void setTitle(String title) {
        properties.set("title", title);
    }

    public void setDescription(String description) {
        properties.set("description", description);
    }

    public void setAim(String aim) {
        properties.set("aim", aim);
    }

    public void setActors(List<Actor> actors) {
        this.conversation.setActors(actors);
    }

    public void addActor(Actor... actors) {
        this.conversation.addActor(actors);
    }


    public void addMessage(Message message) {
        if (this.conversation.getActors().contains(message.getSender()) &&
                (this.conversation.getActors().containsAll(message.getReceivers()) || message.getReceivers() == null)) {
            this.conversation.addMessage(message);
        }
    }

    public void addMessage(Actor sender, List<Actor> receivers, MessageContent content) {
        if (this.conversation.getActors().contains(sender) && receivers != null && this.conversation.getActors().containsAll(receivers)) {
            this.conversation.addMessage(new Message(sender, receivers, content));
        }
    }

    public void addMessage(Actor sender, Actor receiver, MessageContent content) {
        if (this.conversation.getActors().contains(sender) && (this.conversation.getActors().contains(receiver) || receiver == null)) {
            this.conversation.addMessage(new Message(sender, receiver, content));
        }
    }

    public Scene getScene() {
        Scene scene = new Scene(this.properties.get("title").toString());
        scene.setDescription(this.properties.get("description").toString());
        scene.setAim(this.properties.get("aim").toString());
        scene.addConversation(conversation);
        return scene;
    }
}
