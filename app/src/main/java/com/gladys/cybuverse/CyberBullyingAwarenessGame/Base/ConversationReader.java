package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;


import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Utils.GameBase.EventListener;
import com.gladys.cybuverse.Utils.GeneralUtils.collections.Stack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConversationReader {

    public static final String BEFORE_READ_EVENT = "before-read";
    public static final String READ_ERROR_EVENT = "read-error";
    public static final String AFTER_READ_EVENT = "after-read";
    public static final String P_READ_INDEX = "read-index";
    public static final String P_MESSAGE = "message";

    private int readIndex;
    private boolean messageGetterSuccess;
    private Conversation conversation;
    private EventListener listener;

    public ConversationReader(Conversation conversation) {
        this.conversation = conversation;
        this.messageGetterSuccess = false;
        this.readIndex = 0;
    }

    public Conversation getConversation() {
        return conversation;
    }

    private int getMessagesSize() {
        return getMessages().size();
    }

    private List<Message> getMessages() {
        return getConversation().getMessages();
    }

    private Message getMessage(int index) {
        if (index < getMessagesSize()) {
            return getMessages().get(index);
        } else {
            return null;
        }
    }

    public int getReadIndex() {
        return readIndex;
    }

    public void setReadIndex(int readIndex) {
        if (readIndex < getMessagesSize() && readIndex > -1) {
            this.readIndex = readIndex;
        }
    }

    public void incrementReadIndex() {
        readIndex++;
    }

    public void decrementReadIndex() {
        readIndex--;
    }

    public boolean isMessageGetterSuccess() {
        return messageGetterSuccess;
    }

    private void setMessageGetterSuccess(boolean messageGetterSuccess) {
        this.messageGetterSuccess = messageGetterSuccess;
    }

    public EventListener getListener() {
        return listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public boolean hasNext() {
        return (conversation.hasInterruptConversation() && conversation.peekInterruptConversation().hasNext()) ||
                (getReadIndex() < getMessagesSize() && getReadIndex() > -1);
    }

    public Message readNext() {
        if (conversation.hasInterruptConversation()){
            ConversationReader reader = conversation.peekInterruptConversation();
            if (reader.hasNext()){
                Message message = reader.readNext();
                readMessage(message);
                return message;
            }
            else{
                conversation.popInterruptConversation();
                return readNext();
            }
        } else if (hasNext()) {
            Message message = getMessage(getReadIndex());
            incrementReadIndex();
            readMessage(message);
            return message;
        } else {
            if (listener != null) {
                EventListener.Event readEvent = new EventListener.Event(READ_ERROR_EVENT);
                readEvent.addProperty(P_READ_INDEX, getReadIndex());
                listener.processEvent(readEvent);
            }
        }

        return null;
    }

    private void readMessage(Message message) {
        if (listener != null) {
            EventListener.Event readEvent = new EventListener.Event(BEFORE_READ_EVENT);
            readEvent.addProperty(P_READ_INDEX, getReadIndex());
            readEvent.addProperty(P_MESSAGE, message);
            listener.processEvent(readEvent);
        }

        if (message != null) {
            message.read();
        }

        if (listener != null) {
            EventListener.Event readEvent = new EventListener.Event(AFTER_READ_EVENT);
            readEvent.addProperty(P_READ_INDEX, getReadIndex());
            readEvent.addProperty(P_MESSAGE, message);
            listener.processEvent(readEvent);
        }
    }

    public void readAll() {
        while (hasNext()) {
            readNext();
        }
    }

    @Override
    public String toString() {
        return "ConversationReader<" + getConversation() + ">["+getReadIndex()+"]";
    }

}
