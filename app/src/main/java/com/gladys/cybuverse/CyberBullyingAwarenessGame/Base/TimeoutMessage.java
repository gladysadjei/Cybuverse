package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import java.util.List;
import java.util.Map;

public class TimeoutMessage extends InterruptMessage {

    public static final int ACTION_WAIT = 2;

    public TimeoutMessage(Actor sender, List<Actor> receivers, MessageContent content) {
        super(sender, receivers, content);
    }

    public TimeoutMessage(Actor sender, Actor receiver, MessageContent content) {
        super(sender, receiver, content);
    }

    public TimeoutMessage(Actor sender, List<Actor> receivers) {
        super(sender, receivers);
    }

    public TimeoutMessage(Actor sender, Actor receiver) {
        super(sender, receiver);
    }

    public TimeoutMessage() {

    }

    @Override
    public TimeoutMessage setProperties(Map<String, Object> properties) {
        super.setProperties(properties);
        return this;
    }

    @Override
    public TimeoutMessage setProperty(String key, Object value) {
        super.setProperty(key, value);
        return this;
    }

    @Override
    public TimeoutMessage setReceivers(List<Actor> receivers) {
        super.setReceivers(receivers);
        return this;
    }

    @Override
    public TimeoutMessage setSender(Actor sender) {
        super.setSender(sender);
        return this;
    }

    @Override
    public TimeoutMessage setOnReadListener(OnReadListener onReadListener) {
        super.setOnReadListener(onReadListener);
        return this;
    }

}

