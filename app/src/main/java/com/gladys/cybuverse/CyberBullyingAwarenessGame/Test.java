package com.gladys.cybuverse.CyberBullyingAwarenessGame;

import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Actor;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Conversation;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.ConversationReader;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.Message;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageContent;
import com.gladys.cybuverse.CyberBullyingAwarenessGame.Base.MessageGetter;
import com.gladys.cybuverse.Models.Chat;
import com.gladys.cybuverse.Utils.GameBase.EventListener;

public class Test {

    public static void main(String[] args) {

        //simpleTest();

    }

    private static void simpleTest() {
        Conversation conversation = new Conversation(new Chat("Test"));
        Actor ben = new Actor("Ben", 21);
        ben.setRole("main-character");
        Actor grace = new Actor("Grace", 0);
        grace.setRole("ai-character");

        conversation.addActor(ben, grace);
        conversation.addMessage(grace, ben, new MessageContent("Hi"));
        MessageGetter messageGetter = new MessageGetter(ben, grace);
        messageGetter.addContentOptions(new MessageContent("Hi"), new MessageContent("Hello"), new MessageContent("Hey"));
        conversation.addMessage(messageGetter);


        ConversationReader conversationReader = new ConversationReader(conversation);
        conversationReader.setListener(new EventListener() {
            @Override
            public void processEvent(Event event) {
                if (event.getName().equals("before-read")) {
                    readMessage((Message) event.getProperty("message"));
                } else if (event.getName().equals("read-error")) {
                    System.out.println("error: trying to read from index: " + event.getProperty("read-index"));
                }
            }
        });
        conversationReader.readAll();
    }

    private static void readMessage(final Message message) {

        if (message instanceof MessageGetter) {
            ((MessageGetter) message).setListener(new EventListener() {
                @Override
                public void processEvent(Event event) {
                    switch (event.getName()) {
                        case "start-get-message":
                            if (((MessageGetter) message).hasContentOptions()) {
                                System.out.print("message options: ");
                                System.out.println(((MessageGetter) message).getContentOptions());
                            }
                            System.out.print("enter your message: ");
                            break;
                        case "message-received":
                            System.out.println(message.getSender() + " to " + message.getReceivers() + " : " + message.getContent());
                            break;
                        case "message-not-received":
                            System.out.println("no message from player");
                            break;
                    }
                }
            });
            ((MessageGetter) message).waitForMessage();
        } else {
            System.out.println(message.getSender() + " to " + message.getReceivers() + " : " + message.getContent());
        }

    }
}
